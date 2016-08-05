# IRC Radio

Fairly complete IRC client for Android with a focus on TTS.

## Status

Updated some dependencies and got it building with current versions of Android Studio and related tools.


## Planned Stability Enhancements

* Switch to EventBus for background ui updates.
* Move the chatlogs from using listview to viewholer


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

code = language code  any xx, xx_XX, xx_XX_XXX  format is valid and will be paired with matching TTS Resource.


ircMap = Translations used in TTS and chat logs.  When unavailable Logs use English and TTS will subsitute nothing allowing TTS for unsupported languages.


startswithMap = Matches beginning of words for example "www" will replace  "www.reallylonglink.com/df?id=33"  with "web link".

replaceMap = Used to improve experience by substituting acronyms that don't work well in TTS.




