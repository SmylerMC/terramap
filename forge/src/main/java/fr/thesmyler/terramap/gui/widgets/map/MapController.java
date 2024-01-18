package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import net.smyler.smylib.math.Vec2d;
import net.smyler.smylib.math.Vec2dImmutable;
import net.smyler.smylib.math.Vec2dMutable;
import net.smyler.terramap.util.geo.*;
import net.smyler.terramap.util.math.Snapper;

import static fr.thesmyler.terramap.gui.widgets.map.MapWidget.ZOOM_RANGE;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.terramap.util.geo.GeoUtil.getAzimuthInRange;
import static net.smyler.smylib.math.Math.clamp;
import static java.lang.Math.*;

/**
 * Handles moving a map according to its inputs and animations.
 * This is what to use to move a map programmatically.
 *
 * @author SmylerMC
 */
public final class MapController {

    private final MapWidget map;
    MapLayer inputLayer;

    private final GeoPointMutable centerLocation = new GeoPointMutable();
    private final GeoPointMutable centerLocationTarget = new GeoPointMutable();
    private final Vec2dMutable movingSpeed = new Vec2dMutable();
    private float movementDrag = 0.003f;

    // Poles are projected to infinity, not good
    private static final GeoBounds SAFE_BOUNDS = new GeoBounds(
            new GeoPointImmutable(-180d, -89.9999),
            new GeoPointImmutable(180d, 89.9999)
    );
    private GeoBounds bounds; //TODO implement bounds in MapController

    private final GeoPointMutable zoomLocation = new GeoPointMutable();
    private double zoom = 0d;
    private double zoomTarget = 0d;
    private double minZoom = 0d;
    private double maxZoom = 25d;
    private final Snapper zoomSnapper = new Snapper(1d);
    private float zoomResponsiveness = 0.01f;

    private final GeoPointMutable rotateLocation = new GeoPointMutable();
    private float rotation = 0f;
    private float rotationTarget = 0f;
    private final Snapper rotationSnapper = new Snapper(90d, 5d);
    private float rotationResponsiveness = 0.005f;

    private Marker trackedMarker;
    private boolean tracksRotation = false;

    private final Vec2dMutable staticPosition = new Vec2dMutable();
    private final GeoPointMutable staticLocation = new GeoPointMutable();
    private final Vec2dMutable positionCalculationResult = new Vec2dMutable();

    MapController(MapWidget map) {
        this.map = map;
    }

    void update(long dt) {
        /*
         * These things do time dependent integration operations, so if the integration step is irrelevant, skip
         */
        boolean processTimeDependant = dt > 0 && dt < 1000;
        if (processTimeDependant) {
            this.processZoom(dt);
            this.processRotation(dt);
            if (this.trackedMarker == null || this.processTracking()) {
                this.processInertia(dt);
            }
        }
    }

    private void processInertia(long dt) {
        if (!this.centerLocationTarget.equals(this.centerLocation)) {
            this.inputLayer.getPositionOnWidget(this.positionCalculationResult, this.centerLocationTarget);
            double absDx = abs(this.map.getWidth() / 2 - this.positionCalculationResult.x);
            double absDy = abs(this.map.getHeight() / 2 - this.positionCalculationResult.y);
            if (absDx < 1d && absDy < 1d) {
                this.centerLocation.set(this.centerLocationTarget);
                this.movingSpeed.set(Vec2dImmutable.NULL);
            } else {
                this.inputLayer.getPositionOnWidget(this.movingSpeed, this.centerLocation);
                this.movingSpeed.subtract(this.positionCalculationResult).downscale(100);
                double speed = this.movingSpeed.norm();
                double maxSpeed = this.positionCalculationResult.distanceTo(this.map.getWidth() / 2, this.map.getHeight() / 2) / dt;
                if (speed > maxSpeed) this.movingSpeed.downscale(speed).scale(maxSpeed);
            }
        }
        //TODO move the Mouse.isButtonDown(0) out of here
        if(this.movingSpeed.normSquared() > 0d && dt < 1000 && !getGameClient().mouse().isButtonPressed(0)) {
            double dX = this.movingSpeed.x * dt;
            double dY = this.movingSpeed.y * dt;
            this.movingSpeed.scale(max(0d, 1d - this.movementDrag*dt));
            double maxNorm = this.movingSpeed.maximumNorm();
            if (maxNorm < 0.1d) {
                this.movingSpeed.set(Vec2dImmutable.NULL);
            }
            this.stopTracking();
            this.inputLayer.getLocationAtPositionOnWidget(this.centerLocation, this.map.getWidth()/2 - dX, this.map.getHeight()/2 - dY);
            this.inputLayer.updateViewPorts();
        }
    }

    private void processZoom(long dt) {

        double deltaZoom = abs(this.zoom - this.zoomTarget);
        if (deltaZoom <= 0d) return;

        this.setStaticLocation(this.zoomLocation);
        if(deltaZoom < 0.01d) {
            // If we are close enough of the desired zoom level, just finish reaching it
            this.zoom = this.zoomTarget;
        } else {
            // Compute a delta to the new zoom value, exponential decay, and ensure it is within bounds
            double maxDzoom = this.zoomTarget - this.zoom;
            double dzoom = this.zoomResponsiveness * maxDzoom * dt;
            dzoom = maxDzoom > 0 ? min(dzoom, maxDzoom) : max(dzoom, maxDzoom);
            this.movingSpeed.scale(pow(2, dzoom));
            this.zoom += dzoom;
        }
        this.inputLayer.updateViewPorts();
        this.ensureStaticLocationHasNotMoved();
            //TODO Re-implement elsewhere
            //MapWidget.this.rightClickMenu.hide(null);
    }

    private void processRotation(long dt) {

        if (abs(this.rotation - this.rotationTarget) <= 0d) return;

        this.setStaticLocation(this.rotateLocation);

        // Find the shortest way
        float actualRotationTarget = this.rotationTarget;
        float d0 = abs(this.rotationTarget - this.rotation);
        float d1 = abs(this.rotationTarget - this.rotation - 360f);
        float d2 = abs(this.rotationTarget - this.rotation + 360f);
        if(d1 < d0) {
            actualRotationTarget -= 360f;
        } else if(d2 < d0) {
            actualRotationTarget += 360f;
        }

        if(abs(this.rotation - actualRotationTarget) < 0.1f) {
            this.rotation = this.rotationTarget;
        } else {
            float maxDRot = actualRotationTarget - this.rotation;
            float drot = this.rotationResponsiveness * maxDRot * dt;
            drot = maxDRot > 0 ? min(drot, maxDRot) : max(drot, maxDRot);
            this.rotation += drot;
        }

        this.ensureStaticLocationHasNotMoved();
    }

    private boolean processTracking() {
        if (this.trackedMarker.isVisible(this.map)) {
            this.centerLocation.set(this.trackedMarker.getLocation());
            this.centerLocationTarget.set(this.centerLocation);
            this.movingSpeed.set(Vec2dImmutable.NULL);
            if(this.tracksRotation && this.trackedMarker instanceof AbstractMovingMarker) {
                float azimuth = ((AbstractMovingMarker)this.trackedMarker).getAzimuth();
                azimuth = getAzimuthInRange(-azimuth);
                this.rotation = this.rotationTarget = azimuth;
            }
            return false;
        } else {
            this.trackedMarker = null;
            return true;
        }
    }

    /**
     * @return the location at the center of the map
     */
    public GeoPointReadOnly getCenterLocation() {
        return this.centerLocation.getReadOnly();
    }

    /**
     * @return the current zoom of this map
     */
    public double getZoom() {
        return this.zoom;
    }

    /**
     * @return the current rotation of this map
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Increases or decreases this map's zoom of the given amount.
     * The new zoom value will be clamped to this map's maximum and minimum zoom,
     * and snapped to the closest snapping multiple.
     * <br>
     * This method does not interrupt tracking.
     *
     * @param amount    a delta to add to this map zoom's
     * @param animate   whether to transition smoothly to the new value with an animation or to set it immediately
     *
     * @throws IllegalArgumentException if amount is not a finite number
     */
    public void zoom(double amount, boolean animate) {
        if (!Double.isFinite(amount)) throw new IllegalArgumentException("Zoom delta has to be a finite number");
        this.setZoom(this.zoom + amount, animate);
    }

    void dragMap(float dX, float dY, long dt) {
        this.movingSpeed.set(dX, dY).downscale(dt);
        this.moveMap(dX, dY, false);
    }

    /**
     * Moves the map the given amount of pixels.
     *
     * @param dX        how far to move the map along the X axis
     * @param dY        how far to move the map along the Y axis
     * @param animate   whether to transition smoothly to the new position or to set it immediately
     *
     * @throws IllegalArgumentException if either dX or dY is not a finite double
     */
    public void moveMap(double dX, double dY, boolean animate) {
        if (!Double.isFinite(dX) || !Double.isFinite(dY)) throw new IllegalArgumentException("Cannot move the map of a non finite number");
        this.stopTracking();
        this.inputLayer.getPositionOnWidget(this.positionCalculationResult, this.centerLocationTarget);
        this.inputLayer.getLocationAtPositionOnWidget(this.centerLocationTarget, this.positionCalculationResult.subtract(dX, dY));
        if (!animate) {
            this.centerLocation.set(this.centerLocationTarget);
            this.inputLayer.updateViewPorts();
        }
    }

    private void moveCenters(double dX, double dY) {
        this.inputLayer.getPositionOnWidget(this.positionCalculationResult, this.centerLocationTarget);
        this.inputLayer.getLocationAtPositionOnWidget(
                this.centerLocationTarget,
                this.positionCalculationResult.subtract(dX, dY)
        );
        this.inputLayer.getLocationAtPositionOnWidget(
                this.centerLocation,
                this.map.getWidth()/2 - dX,
                this.map.getHeight()/2 - dY);
        // We might end-up reaching the poles if supplied very large numbers because of floating point inaccuracies,
        // which would screw things up because pole get projected to infinities.
        SAFE_BOUNDS.clamp(this.centerLocationTarget);
        SAFE_BOUNDS.clamp(this.centerLocation);
        this.inputLayer.updateViewPorts();
    }

    /**
     * Moves a given location to the center of the map widget.
     * <br>
     * This method interrupts tracking.
     *
     * @param location  a location to move to the center of the map widget
     * @param animate   whether to transition smoothly to the new center with an animation or to set it immediately
     *
     * @throws NullPointerException if location is null
     */
    public void moveLocationToCenter(GeoPoint<?> location, boolean animate) {
        this.moveLocationToPosition(location, this.map.getWidth() / 2d, this.map.getHeight() / 2d, animate);
    }

    /**
     * Moves a given location to a given position on the map widget.
     * <br>
     * This method interrupts tracking.
     *
     * @param location  a location to move to the center of the map widget
     * @param position  a position on the map widget to move the location to
     * @param animate   whether to transition smoothly to the new center with an animation or to set it immediately
     *
     * @throws IllegalArgumentException if either the x or y component of position is not a finite number
     * @throws NullPointerException     if either location or position is null
     */
    public void moveLocationToPosition(GeoPoint<?> location, Vec2d<?> position, boolean animate) {
        this.moveLocationToPosition(location, position.x(), position.y(), animate);
    }

    /**
     * Moves a given location to a given position on the map widget.
     * <br>
     * This method interrupts tracking.
     *
     * @param location  a location to move to the center of the map widget
     * @param x         an X coordinate on the map widget to move the location to
     * @param y         a Y coordinate on the map widget to move the location to
     * @param animate   whether to transition smoothly to the new center with an animation or to set it immediately
     *
     * @throws IllegalArgumentException if either x or y is not a finite number
     * @throws NullPointerException if location is null
     */
    public void moveLocationToPosition(GeoPoint<?> location, double x, double y, boolean animate) {
        this.inputLayer.getPositionOnWidget(this.positionCalculationResult, location);
        this.positionCalculationResult.subtract(x, y).scale(-1d);
        this.moveMap(this.positionCalculationResult.x, this.positionCalculationResult.y, animate);
    }

    private void setStaticLocation(GeoPoint<?> point) {
        this.staticLocation.set(point);
        this.inputLayer.getPositionOnWidget(this.staticPosition, this.staticLocation);
    }

    private void ensureStaticLocationHasNotMoved() {
        this.inputLayer.getPositionOnWidget(this.positionCalculationResult, this.staticLocation);
        this.positionCalculationResult.subtract(this.staticPosition).scale(-1d);
        this.moveCenters(this.positionCalculationResult.x, this.positionCalculationResult.y);
    }

    /**
     * Changes this map's zoom to the given value.
     * The new zoom value will be clamped to this map's maximum and minimum zoom,
     * and snapped to the closest snapping multiple.
     * <br>
     * This method does not interrupt tracking.
     *
     * @param zoom      a new zoom value
     * @param animate   whether to transition smoothly to the new value with an animation or to set it immediately
     *
     * @throws IllegalArgumentException if zoom is not a finite number
     */
    public void setZoom(double zoom, boolean animate) {
        if (!Double.isFinite(zoom)) throw new IllegalArgumentException("Zoom has to be a finite number");
        this.zoomTarget = clamp(this.zoomSnapper.snap(zoom), this.minZoom, this.maxZoom);
        if (!animate) {
            this.setStaticLocation(this.zoomLocation);
            this.zoom = this.zoomTarget;
            this.inputLayer.updateViewPorts();
            this.ensureStaticLocationHasNotMoved();
        }
    }

    /**
     * Sets a location that should stay static when this map's zoom changes.
     *
     * @param location a location to keep static on the map widget when zooming
     *
     * @throws NullPointerException if location is null
     */
    public void setZoomStaticLocation(GeoPoint<?> location) {
        this.zoomLocation.set(location);
        this.inputLayer.updateViewPorts();
    }

    /**
     * Sets a location that should stay static when this map's zoom changes,
     * using its position on the map widget.
     *
     * @param position a position on the map widget,
     *                 under which the corresponding location should stay static on the map widget when zooming
     *
     * @throws NullPointerException if position is null
     * @throws IllegalArgumentException if position is not finite
     */
    public void setZoomStaticPosition(Vec2d<?> position) {
        if (!position.isFinite()) throw new IllegalArgumentException("Zoom static position needs to be finite");
        this.inputLayer.getLocationAtPositionOnWidget(this.zoomLocation, position);
    }

    /**
     * Sets a location that should stay static when this map's zoom changes,
     * using its position on the map widget.
     *
     * @param x a position X coordinate on the map widget,
     *          under which the corresponding location should stay static on the map widget when zooming
     * @param y a position Y coordinate on the map widget,
     *          under which the corresponding location should stay static on the map widget when zooming
     */
    public void setZoomStaticPosition(float x, float y) {
        this.inputLayer.getLocationAtPositionOnWidget(this.zoomLocation, x, y);
    }

    /**
     * @return the location that stays static when this map's zoom changes
     */
    public GeoPointReadOnly getZoomStaticLocation() {
        return this.zoomLocation.getReadOnly();
    }

    /**
     * Changes this map's rotation to a new value.
     * The new value will be converted to be in the [0°, 360°] range.
     *
     * @param rotation  a new rotation value
     * @param animate   whether to transition smoothly to the new value with an animation or to set it immediately
     */
    public void setRotation(float rotation, boolean animate) {
        if (!Float.isFinite(rotation)) throw new IllegalArgumentException("Layer rotation has to be a finite number");
        this.rotationTarget = getAzimuthInRange(this.rotationSnapper.snap(rotation));
        if (!animate) {
            this.setStaticLocation(this.rotateLocation);
            this.rotation = this.rotationTarget;
            this.inputLayer.updateViewPorts();
            this.ensureStaticLocationHasNotMoved();
        }
    }

    /**
     * Sets a location that should stay static when this map rotates.
     *
     * @param location a location to keep static on the map widget when rotating
     *
     * @throws NullPointerException if location is null
     */
    public void setRotationStaticLocation(GeoPoint<?> location) {
        this.rotateLocation.set(location);
        this.inputLayer.updateViewPorts();
    }

    /**
     * Sets a location that should stay static when this map rotates,
     * using its position on the map widget.
     *
     * @param position a position on the map widget,
     *                 under which the corresponding location should stay static on the map widget when rotating
     *
     * @throws NullPointerException if position is null
     * @throws IllegalArgumentException if position is not finite
     */
    public void setRotationStaticPosition(Vec2d<?> position) {
        if (!position.isFinite()) throw new IllegalArgumentException("Static rotation position has to be finite");
        this.inputLayer.getLocationAtPositionOnWidget(this.rotateLocation, position);
    }

    /**
     * Sets a location that should stay static when this map rotates,
     * using its position on the map widget.
     *
     * @param x a position X coordinate on the map widget,
     *          under which the corresponding location should stay static on the map widget when rotating
     * @param y a position Y coordinate on the map widget,
     *          under which the corresponding location should stay static on the map widget when rotating
     *
     * @throws IllegalArgumentException if either X or Y is not finite
     */
    public void setRotationStaticPosition(double x, double y) {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Static rotation position has to be finite");
        }
        this.inputLayer.getLocationAtPositionOnWidget(this.rotateLocation, x, y);
    }

    /**
     * @return the location that stays static when the map rotates
     */
    public GeoPointReadOnly getRotationStaticLocation() {
        return this.rotateLocation.getReadOnly();
    }

    /**
     * Stops any zooming animation, freezing the zoom at its current value.
     */
    public void stopZooming() {
        this.zoomTarget = this.zoom;
    }

    /**
     * Stops any moving animation, freezing the vue at its current position.
     */
    public void stopPanning() {
        this.centerLocationTarget.set(this.centerLocation);
        this.movingSpeed.set(Vec2dImmutable.NULL);
    }

    /**
     * Stops any rotation animation, freezing the rotation at its current value.
     */
    public void stopRotating() {
        this.rotationTarget = this.rotation;
    }

    /**
     * Interrupts all map movement.
     * Freezes any ongoing animation.
     */
    public void stopRightThere() {
        this.stopZooming();
        this.stopPanning();
        this.stopRotating();
        this.stopTracking();
    }

    /**
     * Start tracking a marker.
     *
     * @param marker    the marker to track
     *
     * @throws NullPointerException if marker is null
     */
    public void track(Marker marker) {
        if (marker == null) throw new NullPointerException("Cannot track a null marker");
        this.trackedMarker = marker;
    }

    /**
     * @return whether this controller is currently tracking a marker
     */
    public boolean isTracking() {
        return this.trackedMarker != null;
    }

    /**
     * Stops tracking the currently tracked marker, if there is one.
     */
    public void stopTracking() {
        this.trackedMarker = null;
    }

    /**
     * @return whether this map follows the rotation of the markers it tracks
     */
    public boolean tracksRotation() {
        return this.tracksRotation;
    }

    /**
     * Changes whether this map should follow the rotation of markers it tracks.
     *
     * @param yesNo whether to follow rotations
     */
    public void setTracksRotation(boolean yesNo) {
        this.tracksRotation = yesNo;
    }

    /**
     * @return the marker which is currently being tracked, or null if none is being tracked
     */
    public Marker getTrackedMarker() {
        return this.trackedMarker;
    }

    /**
     * @return the minimum value this controller will allow the zoom to take
     */
    public double getMinZoom() {
        return this.minZoom;
    }

    /**
     * Sets the minimum value this controller will allow the zoom to take.
     *
     * @param minZoom   the value
     *
     * @throws IllegalArgumentException if minZoom is not in the [0, 25] range
     */
    public void setMinZoom(double minZoom) {
        if (!Double.isFinite(minZoom) || !ZOOM_RANGE.matches(minZoom)) {
            throw new IllegalArgumentException("Minimum zoom shall be between 0 and 25 inclusive. Not " + minZoom);
        }
        this.minZoom = minZoom;
    }

    /**
     * @return the maximum value this controller will allow the zoom to take
     */
    public double getMaxZoom() {
        return this.maxZoom;
    }

    /**
     * Sets the maximum value this controller will allow the zoom to take.
     *
     * @param maxZoom   the value
     *
     * @throws IllegalArgumentException if maxZoom is not in the [0, 25] range
     */
    public void setMaxZoom(double maxZoom) {
        if (!Double.isFinite(maxZoom) || !ZOOM_RANGE.matches(maxZoom)) {
            throw new IllegalArgumentException("Maximum zoom shall be between 0 and 25 inclusive. Not " + maxZoom);
        }
        this.maxZoom = maxZoom;
    }

    /**
     * Sets the value the zoom level should be a multiple of.
     *
     * @param snapping  the value
     * @throws IllegalArgumentException if the given value is strictly negative
     */
    public void setZoomSnapping(double snapping) {
        this.zoomSnapper.setPeriod(snapping);
    }

    /**
     * @return the value the zoom level should be a multiple of
     */
    public double getZoomSnapping() {
        return this.zoomSnapper.getPeriod();
    }

    /**
     * @return the location the map is moving towards
     */
    public GeoPointReadOnly getTargetLocation() {
        return this.centerLocationTarget.getReadOnly();
    }

    /**
     * @return the zoom value the map is moving towards
     */
    public double getTargetZoom() {
        return this.zoomTarget;
    }

    /**
     * @return the rotation value this map is moving towards
     */
    public float getTargetRotation() {
        return this.rotationTarget;
    }

    /**
     * @return the drag coefficient that slows the map down when it is sliding free.
     */
    public float getMovementDrag() {
        return this.movementDrag;
    }

    /**
     * Sets the drag coefficient that should be applied to slow down the map when it is sliding free.
     *
     * @param drag the coefficient, 0 means no drag and 1 sliding
     *
     * @throws IllegalArgumentException is drag is not a positive number
     */
    public void setMovementDrag(float drag) {
        if (!Float.isFinite(drag) || drag < 0) {
            throw new IllegalArgumentException("Movement drag shall be positive, not " + drag);
        }
        this.movementDrag = drag;
    }

    /**
     * @return the responsiveness coefficient when zooming on the map. Higher means faster.
     */
    public float getZoomResponsiveness() {
        return this.zoomResponsiveness;
    }

    /**
     * Sets the responsiveness coefficient when zooming on the map. Higher means faster
     *
     * @param zoomResponsiveness the new coefficient
     *
     * @throws IllegalArgumentException if zoomResponsiveness is not a finite number
     */
    public void setZoomResponsiveness(float zoomResponsiveness) {
        if (!Float.isFinite(zoomResponsiveness) || zoomResponsiveness < 0) {
            throw new IllegalArgumentException("Zoom responsiveness has to be positive number not " + zoomResponsiveness);
        }
        this.zoomResponsiveness = zoomResponsiveness;
    }

    /**
     * @return the responsiveness coefficient when rotating the map. Higher means faster
     */
    public float getRotationResponsiveness() {
        return this.rotationResponsiveness;
    }

    /**
     * Sets the responsiveness coefficient when rotating the map. Higher means faster
     *
     * @param rotationResponsiveness the new coefficient
     *
     * @throws IllegalArgumentException if rotationResponsiveness is not a finite number
     */
    public void setRotationResponsiveness(float rotationResponsiveness) {
        if (!Float.isFinite(rotationResponsiveness) || rotationResponsiveness < 0) {
            throw new IllegalArgumentException("Rotation responsiveness has to be positive number, not " + rotationResponsiveness);
        }
        this.rotationResponsiveness = rotationResponsiveness;
    }

}
