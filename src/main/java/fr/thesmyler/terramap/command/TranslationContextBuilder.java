package fr.thesmyler.terramap.command;

import fr.thesmyler.terramap.TerramapVersion;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class TranslationContextBuilder {

	// First version that supports translation in this context
	private TerramapVersion translationVersion;

	public TranslationContextBuilder(TerramapVersion minVersion) {
		this.translationVersion = minVersion;
	}

	public TranslationContextBuilder() {
		this.translationVersion = null;
	}

	public TranslationContext createNewContext(ICommandSender sender) {
		return new TranslationContext(sender);
	}

	public class TranslationContext {

		public final TerramapVersion senderVersion;
		public final boolean senderSupportsTranslation;

		private TranslationContext(ICommandSender sender) {
			if(sender instanceof EntityPlayerMP) {
				this.senderVersion = TerramapVersion.getClientVersion((EntityPlayerMP) sender);
			} else {
				this.senderVersion = null;
			}
			if(TranslationContextBuilder.this.translationVersion != null) {
				this.senderSupportsTranslation = TranslationContextBuilder.this.translationVersion.isOlderOrSame(this.senderVersion);
			} else {
				this.senderSupportsTranslation = false;
			}
		}

		public ITextComponent getComponent(String key, Object... objects) {
			TextComponentTranslation translationComponent = new TextComponentTranslation(key, objects);
			if(this.senderSupportsTranslation) {
				return translationComponent;
			} else {
				return new TextComponentString(translationComponent.getFormattedText());
			}
		}

		public String getText(String key) {
			if(this.senderSupportsTranslation) {
				return key;
			} else {
				return I18n.translateToLocal(key);
			}
		}
		
		public void syntaxException(String key) throws SyntaxErrorException {
			throw new SyntaxErrorException(this.getText(key));
		}

		public void playerNotFoundException(String key) throws PlayerNotFoundException {
			throw new PlayerNotFoundException(this.getText(key));
		}
		
		public void commandException(String key) throws CommandException {
			throw new CommandException(this.getText(key));
		}

	}
}
