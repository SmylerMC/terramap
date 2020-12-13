package fr.thesmyler.terramap.caching;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.caching.requests.CachedRequest;
import fr.thesmyler.terramap.caching.requests.FailedRequest;
import fr.thesmyler.terramap.caching.requests.HttpRequest;
import fr.thesmyler.terramap.caching.requests.SuccessfulCachedRequest;
import fr.thesmyler.terramap.config.TerramapConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Terramap's own implementation of a {@link CacheManager}
 * 
 * @author SmylerMC
 *
 */
public class CacheManagerTerramap implements CacheManager {

	// How many threads can be downloading from the same server at the same time
	private static final int MAX_CONCURRENT_DOWNLOAD = 2;
	private final Map<String, AtomicInteger> threadCounts = new HashMap<>();

	// Used to name threads
	private static AtomicLong threadNumberingGet = new AtomicLong(0);
	private static AtomicLong threadNumberingCache = new AtomicLong(0);

	private final AtomicInteger waitingRequests = new AtomicInteger(0);

	private File cacheRoot;
	private File cacheDbFile;
	private String dbUrl;
	private Connection dbConnection;

	private static final String DB_METADATA_TABLE = "metadata";
	private static final String DB_METADATA_CACHE_VER_KEY = "cacheVersion";
	private static final String DB_METADATA_TM_VER_KEY = "terramapVersion";
	private static final String SQL_CREATE_METADATA_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_METADATA_TABLE + " ("
									+ "	id integer PRIMARY KEY, "
									+ DB_METADATA_CACHE_VER_KEY + " integer NOT NULL, "
									+ DB_METADATA_TM_VER_KEY + " text NOT NULL"
									+ ");";
	private static final String DB_CACHE_TABLE = "cache";
	private static final String DB_CACHE_URL_KEY = "resource";
	private static final String DB_CACHE_TIME_KEY = "time";
	private static final String SQL_CREATE_CACHE_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_CACHE_TABLE + " ("
									+ DB_CACHE_URL_KEY + " text PRIMARY KEY, "
									+ DB_CACHE_TIME_KEY + " date NOT NULL"
									+ ");";
	private static final String SQL_INSERT_METADATA = "INSERT INTO " + DB_METADATA_TABLE + " ("
									+ "id, "
									+ DB_METADATA_CACHE_VER_KEY + ", "
									+ DB_METADATA_TM_VER_KEY + ") VALUES "
									+ "(0, ?, ?);";
	private static final String SQL_UPDATE_METADATA = "UPDATE " + DB_METADATA_TABLE + " SET"
			+ DB_METADATA_CACHE_VER_KEY + "=?, "
			+ DB_METADATA_TM_VER_KEY + "=? WHERE id=0";
	
	private static final int CACHE_VERSION = 0; //TODO Bump cache version to 1 on release

	@Override
	public void setup() {
		File cacheRoot = new File(TerramapConfig.cachingDir);
		//TODO Detect an old cache and propose to remove it
		this.setupCacheRoot(cacheRoot);
		this.cacheDbFile = new File(cacheRoot.getAbsolutePath() + File.separator + "cache.db");
		this.setupCacheDb(this.cacheDbFile);
	}

	private void setupCacheRoot(File cacheRoot) {
		this.cacheRoot = cacheRoot;
		boolean isCacheRootValid = true;
		try {
			if(!this.cacheRoot.exists()) {
				try {
					this.cacheRoot.mkdirs();
					TerramapMod.logger.info("Created a new cache directory as it did not exist");
				} catch(SecurityException e) {
					isCacheRootValid = false;
					TerramapMod.logger.error("Failed to create the cache directory!");
					TerramapMod.logger.catching(e);
				}
			}
		} catch(SecurityException e) {
			isCacheRootValid = false;
			TerramapMod.logger.error("Failed to check if the cache root existed!");
		}
		if(!this.cacheRoot.isDirectory()) {
			isCacheRootValid = false;
			TerramapMod.logger.error("The cache directory is not a directory!");
		}
		if(!this.cacheRoot.canRead()) {
			isCacheRootValid = false;
			TerramapMod.logger.error("The cache directory is not readable!");
		}
		if(!this.cacheRoot.canWrite()) {
			isCacheRootValid = false;
			TerramapMod.logger.error("The cache directory is not writable!");
		}
		if(!isCacheRootValid) {
			TerramapMod.logger.error("The cache directory was not valid, trying to use a temporary directory.");
			try {
				this.cacheRoot = File.createTempFile("terramap", "tempcache");
			} catch (IOException e) {
				TerramapMod.logger.fatal("Failed to create a fallback temporary cache directory! The game will crash.");
				TerramapMod.logger.catching(e);
				throw new RuntimeException();
			}
		}
	}

	private void setupCacheDb(File file) {
		this.dbUrl = "jdbc:sqlite:" + this.cacheDbFile.getAbsolutePath();
		try {
			this.dbConnection = DriverManager.getConnection(this.dbUrl);
			this.dbConnection.setAutoCommit(true);
			if(!this.testMetadataTable()) {
				TerramapMod.logger.error("The cache database was incompatible or corrupted, reseting cache.");
				//TODO Show a GUI prompt before removing files
				
			}
			
		} catch (SQLException e) {
			// TODO Handle method in CacheManager::setupCacheDb
			TerramapMod.logger.catching(e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup, "Terramap cache cleanup"));
	}

	private boolean testMetadataTable() throws SQLException {
		synchronized(this.dbConnection) {
			try(Statement statement = this.dbConnection.createStatement()) {
				
				// Create tables
				statement.execute(SQL_CREATE_METADATA_TABLE);
				statement.execute(SQL_CREATE_CACHE_TABLE);
				
				// Test columns data types of the metadata table
				DatabaseMetaData dbMeta = this.dbConnection.getMetaData();
				ResultSet cols = dbMeta.getColumns(null, null, DB_METADATA_TABLE, DB_METADATA_CACHE_VER_KEY);
				if(!(cols.next() && java.sql.Types.INTEGER == cols.getInt("DATA_TYPE"))) return false;
				cols = dbMeta.getColumns(null, null, DB_METADATA_TABLE, DB_METADATA_TM_VER_KEY);
				if(!(cols.next() && java.sql.Types.VARCHAR == cols.getInt("DATA_TYPE"))) return false;
				
				// Test columns data types of the cache table
				cols = dbMeta.getColumns(null, null, DB_CACHE_TABLE, DB_CACHE_URL_KEY);
				if(!(cols.next() && java.sql.Types.VARCHAR == cols.getInt("DATA_TYPE"))) return false;
				cols = dbMeta.getColumns(null, null, DB_CACHE_TABLE, DB_CACHE_TIME_KEY);
				if(!(cols.next() && java.sql.Types.VARCHAR == cols.getInt("DATA_TYPE"))) return false;
				
				boolean updateMeta = false;
				// Check if the cache was of a newer version
				ResultSet res = statement.executeQuery("SELECT " + DB_METADATA_CACHE_VER_KEY + ", " + DB_METADATA_TM_VER_KEY + " FROM " + DB_METADATA_TABLE + " WHERE id=0;");
				if(res.next()) {
					int cacheVersion = res.getInt(DB_METADATA_CACHE_VER_KEY);
					String lastTMVersion = res.getString(DB_METADATA_TM_VER_KEY);
					TerramapMod.logger.info("Cache metadata: Cache version: ", cacheVersion, ". Last used Terramap version: ", lastTMVersion);
					if(cacheVersion > CACHE_VERSION) {
						TerramapMod.logger.error("The cache was saved with a newer version and cannot be used. In metadata: " + cacheVersion + "(Terramap version " + lastTMVersion + ")" + " Expecting: " + CACHE_VERSION);
						return false;
					}
					updateMeta = true;
				}

				if(updateMeta) {
					try(PreparedStatement preStatement = this.dbConnection.prepareStatement(SQL_UPDATE_METADATA)) {
						preStatement.setInt(1, CACHE_VERSION);
						preStatement.setString(2, TerramapMod.getVersion().toString());
						preStatement.executeUpdate();
					}
				} else {
					try(PreparedStatement preStatement = this.dbConnection.prepareStatement(SQL_INSERT_METADATA)) {
						preStatement.setInt(1, CACHE_VERSION);
						preStatement.setString(2, TerramapMod.getVersion().toString());
						preStatement.executeUpdate();
					}
				}
				
			}
		}
		return true;
	}

	private void cleanup() {
		TerramapMod.logger.debug("Closing cache db");
		if(this.dbConnection != null) {
			try {
				this.dbConnection.close();
			} catch (SQLException e) {
				TerramapMod.logger.error("Failed to save cache database, things may not have been saved properly!");
				TerramapMod.logger.catching(e);
			}
		}
	}

	/**
	 * Gets the given url in a new thread, either by downloading it or by loading it from the cache,
	 * then calls the given consumer from that thread.
	 * 
	 * @param url
	 * @param callback
	 * 
	 * @return
	 */
	@Override
	public QueuedCacheTask getAsync(URL url, Consumer<CachedRequest> callback) {
		String host = url.getAuthority();
		Runnable action;
		QueuedCacheTask task = new QueuedCacheTask();
		if(!host.equals("")) {
			action = () -> {
				this.waitingRequests.getAndIncrement();
				AtomicInteger currentActions;
				synchronized(this.threadCounts) {
					currentActions = threadCounts.get(host);
					if(currentActions == null) {
						currentActions = new AtomicInteger(0);
					}
					threadCounts.put(host, currentActions);
				}
				boolean incremented = false;
				try {
					while(!task.isCanceled() && currentActions.get() >= MAX_CONCURRENT_DOWNLOAD) {
						synchronized(currentActions) {
							currentActions.wait();
						}
					}
					if(task.isCanceled()) return;
					currentActions.incrementAndGet();
					incremented = true;
					CachedRequest data = this.get(url);
					task.setResult(data);
					callback.accept(data);
				} catch(Exception e) {
					CachedRequest data= new FailedRequest(url, e, false);
					task.setResult(data);
					callback.accept(data);
				} finally {
					if(incremented) currentActions.decrementAndGet();
					synchronized(currentActions) {
						currentActions.notify();
					}
					this.waitingRequests.getAndDecrement();
				}
			};
		} else { // This is local, process immediately
			action = () -> {
				this.waitingRequests.getAndIncrement();
				try {
					CachedRequest data = this.get(url);
					task.setResult(data);
					callback.accept(data);
				} catch(Exception e) {
					CachedRequest data = new FailedRequest(url, e, false);
					task.setResult(data);
					callback.accept(data);
				} finally {
					this.waitingRequests.decrementAndGet();
				}
			};
		}
		Thread t = new Thread(action);
		t.setName("Terramap download " + threadNumberingGet.getAndIncrement());
		t.setDaemon(true);
		t.start();
		return task;
	}

	@Override
	public CachedRequest get(URL url) {
		TerramapMod.logger.info(url);
		boolean isNetwork = false;
		try {
			//TODO Do not make a request if the cached version is recent enough
			URLConnection connection = url.openConnection();
			connection.setAllowUserInteraction(false);
			connection.setRequestProperty("User-Agent", TerramapMod.getUserAgent());
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.setInstanceFollowRedirects(true);
				isNetwork = true;
			}

			// TODO connection.setIfModifiedSince(ifmodifiedsince);

			ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer();
			connection.connect();

			boolean errorStream = false;
			int code = -1;
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				code = httpConnection.getResponseCode();
				if(code >= 400 && code < 600) errorStream = true;
			}
			try(InputStream inStream = errorStream? ((HttpURLConnection)connection).getErrorStream(): connection.getInputStream()) {
				do {
					buf.ensureWritable(1024);
				} while (buf.writeBytes(inStream, 1024) > 0);
			}

			if (code > 0) {
				switch(code) {
				case HttpURLConnection.HTTP_NOT_MODIFIED:
					return this.readFromCache(url);
				default:
					byte[] read = new byte[buf.writerIndex()];
					buf.readBytes(read);
					CachedRequest req = new HttpRequest(url, code, read);
					this.cacheAsync(req);
					return req;
				}
			}

		} catch (Exception e) {
			return new FailedRequest(url, e, isNetwork);
		}
		return new FailedRequest(url, new Exception(), isNetwork);
	}

	@Override
	public int getQueueSize() {
		return this.waitingRequests.get();
	}

	public CachedRequest readFromCache(URL url) {
		return new SuccessfulCachedRequest(url, new byte[0]); //TODO ReadFromCache
	}

	private void cache(CachedRequest data) {
		//TODO CacheManager::cache
	}

	private void cacheAsync(CachedRequest data) {
		Thread t = new Thread(() ->  {this.cache(data);});
		t.setName("Terramap caching " + threadNumberingCache.getAndIncrement());
		t.setDaemon(true);
		t.start();
	}
	
	public static void main(String args[]) {
		String[] urls = {
				"http://localhost/index.php?test01",
				"http://localhost/index.php?test02",
				"http://localhost/index.php?test03",
				"http://localhost/index.php?test04",
				"http://localhost/index.php?test05",
				"http://localhost/index.php?test06",
				"http://localhost/index.php?test07",
				"http://localhost/index.php?test08",
				"http://localhost/index.php?test09",
				"http://localhost/index.php?test10",
				"http://localhost:80/index.php?test11",
				"http://localhost/index.php?test12",
				"http://localhost/index.php?test13",
				"http://localhost/index.php?test14",
				"http://localhost/index.php?test15",
				"http://localhost/index.php?test16",
				"http://localhost/index.php?test17",
				"http://localhost/index.php?test18",
				"http://example.com/",
				"http://localhost:80/index.php?test19"
		};
		CacheManager m = new CacheManagerTerramap();
		for(String s: urls) {
			URL url;
			try {
				url = new URL(s);
				m.getAsync(url, (d) -> {
					System.out.println((d.wasSuccessful()? "OK": "Nop") + " => " + d.getUrl());
					if(!d.wasSuccessful()) {
						Object e = d.getError();
						if(e instanceof Exception) ((Exception) e).printStackTrace();
						else if(d instanceof HttpRequest) System.out.println(((HttpRequest)d).getStatusCode());
					}
				});
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
