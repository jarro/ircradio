# IRC Radio

Fairly complete IRC client for Android with a focus on TTS.

## Status

Updated some dependencies and got it building with current versions of Android Studio and related tools.


## Planned Stability Enhancements

* Switch to EventBus for background ui updates.
* Move the chatlogs from using listview to viewholder


## TTS INFO

### Supported Languages
- English
- French
- Spanish
- Italian
- German

Any Installed TTS Engine will work in a limited capacity.


### Support Additional languages
Language resource file similar to template.jsn required for full support.


#### Specs
Language tables are in json format.
(https://github.com/jarro/ircradio/tree/master/iRCRADIO/src/main/assets/lookup)
check examples.jsn for formatting

code = language code  any xx, xx_XX, xx_XX_XXX  format is valid and will be paired with matching TTS Resource.


ircMap = Translations used in TTS and chat logs.  When unavailable Logs use English and TTS will subsitute nothing allowing TTS for unsupported languages.


startswithMap = Matches beginning of words for example "www" will replace  "www.reallylonglink.com/df?id=33"  with "web link".


replaceMap = Used to improve experience by substituting acronyms that don't work well in TTS.


blockMessageByStartsWith = Will drop message from TTS if it starts with provided text.


blockMessageByUser = Block all messages from a user or bot.


blockMessageByRegex = regex is passed to String.match  If it evaluates to true message is not sent to TTS


replaceAllByRegex = requires regex and the text to sub in.   uses  the  String.replaceAll function



replaceAllByRegex = calls java's  String.replaceAll  takes regex and a string.



#### Using Custom Language File
All language files are copied to public storage on app start if the file does not already exist.
If a language file has been loaded into memory the app will need to be restarted for edits to take affect.
The files are stored in the apps public files folder.
Android/data/com.earthflare.android.ircradio/files/lookup

Delete a file to have it replaced.
Normally Restarting app will refresh customizations.
Sometimes a reboot of the device is neccessary for file changes to be available.



