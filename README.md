LaunchOnWakeUp is  Android TV launcher. It is intended to be light-weighted layer for starting desired app every time you turn on TV (wakeup mode).

It was tested only with Android TV Asano (40LF7030S)

Make:
1. Command line
`gradle build`

you get  app-release-unsigned.apk

if tou need sign then
get sign file if you don't have by
`keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 100000`
sign it

or "Run app" from android studio.