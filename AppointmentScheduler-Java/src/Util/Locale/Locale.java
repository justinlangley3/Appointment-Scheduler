/*
 * Created By: 	Justin A. Langley
 * Course: 	C195 - Software II 
 * Language: 	Java
 *
 * @(#)Locale.java   2021.05.20 at 12:38:03 EDT
 * Version: 1.0.0
 */



package Util.Locale;

import java.io.IOException;

import java.util.Properties;

//~--- classes ----------------------------------------------------------------

public final class Locale
{
    private static final Locale LOCALE = new Locale();

    //~--- fields -------------------------------------------------------------

    String       locale;
    private Lang LANG;

    //~--- constant enums -----------------------------------------------------

    public enum Lang { EN, ES }

    //~--- constructors -------------------------------------------------------

    private Locale()
    {

        /*
         * This object is a singleton object which can be accessed anywhere within the program
         * The only way to automatically detect the system locale is by ensuring correct OS configuration
         * This constructor works out of the box on a fresh Windows install. If settings are changed after installation,
         * please follow minimum reproducible steps below to ensure appropriate locale detection.
         *
         * Minimal reproducible steps:
         *  Control Panel -> Region -> Administrative Tab -> Change System Locale (requires system restart)
         *      - Valid locales are: Spanish (Spain), English (United States)
         *        if locale is already in a locale with Spanish language: espanol (Espana), Ingles (Estados Unidos)
         *
         *      Next Region and input language must be set. This can be done with:
         *      - (Win + I) -> Time & Language
         *          Note: Changing Language will require logging out and then logging back in after applying the setting
         *          * - Language -> Add a language -> select language for supported locale (download must complete before continuing)
         *          * - Language -> Windows Display -> select supported display language
         *          * - Region -> Regional -> select supported region
         *          * - Region -> Regional Format -> select supported region format
         */

        // This line was used for testing: System.out.println(System.getProperty("user.language") + "-" + System.getProperty("user.country"));
        switch (System.getProperty("user.language") + "-" + System.getProperty("user.country")) {
          case "en-US" :
              locale = "en-US";
              LANG   = Lang.EN;

              return;

          case "es-ES" :
              locale = "es-ES";
              LANG   = Lang.ES;

              return;

          default :
              locale = "en-US";
              LANG   = Lang.EN;

              return;
        }
    }

    //~--- methods ------------------------------------------------------------

    public String val(String key) throws IOException
    {
        Properties props = new Properties();
        String     path;

        switch (LANG) {
          case EN :
              path = "locale_en_US.properties";

              props.load(Locale.class.getClassLoader()
                                     .getResourceAsStream(path));

              break;

          case ES :
              path = "locale_es_ES.properties";

              props.load(Locale.class.getClassLoader()
                                     .getResourceAsStream(path));

              break;

          default :
              path = "locale_en_US.properties";

              props.load(Locale.class.getClassLoader()
                                     .getResourceAsStream(path));

              break;
        }

        return props.getProperty(key);
    }

    //~--- get methods --------------------------------------------------------

    public static Locale get()
    {
        return LOCALE;
    }

    public String getLocale()
    {
        return this.locale;
    }

    //~--- set methods --------------------------------------------------------

    public void setLocale(String locale)
    {
        this.locale = locale;

        switch (locale) {
          case "en-US" :
              this.LANG = Lang.EN;

              break;

          case "es-ES" :
              this.LANG = Lang.ES;

              break;
        }
    }
}

/*
 * @(#)Locale.java 2021.05.20 at 12:38:03 EDT
 * EOF
 */
