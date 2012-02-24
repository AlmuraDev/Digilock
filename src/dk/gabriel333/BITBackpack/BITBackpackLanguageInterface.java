package dk.gabriel333.BITBackpack;

public class BITBackpackLanguageInterface {
	public Language					language	= Language.ENGLISH;

	private BITBackpackLanguageInterface_EN	languageEN	= new BITBackpackLanguageInterface_EN();
	private BITBackpackLanguageInterface_FR	languageFR	= new BITBackpackLanguageInterface_FR();

	public Language getLanguage() {
		return language;
	}

	public BITBackpackLanguageInterface(Language language2) {
		language = language2;
	}

	public String getMessage(String key) {
		switch (language) {
		case ENGLISH:
			return languageEN.getString(key);
		case FRENCH:
			return languageFR.getString(key);
		default:
			return languageEN.getString(key);
		}
	}

	public enum Language {
		ENGLISH, FRENCH;

		@Override
		public String toString() {
			return super.toString();
		}
	}
}
