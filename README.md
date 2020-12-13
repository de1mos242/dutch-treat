# Dutch treat
Dutch treat app

# What's new:

## 0.0.8
1. Add sentry configs
2. Refactoring [#9](https://github.com/de1mos242/dutch-treat/issues/9)

## 0.0.7
1. Moved to ktor + koin [#15](https://github.com/de1mos242/dutch-treat/issues/15)

## 0.0.6-SNAPSHOT
1. Moved to Google Cloud Run

## 0.0.5-SNAPSHOT
1. Add Sentry integration

## 0.0.4-SNAPSHOT
1. Move from jaicp cloud to Heroku

## 0.0.3-SNAPSHOT
1. Added dialog flow integration. Try ask about balance like `show me current state` or even in russian: `Что по деньгам`

## 0.0.2-SNAPSHOT
1. Added `remove purchase` command. See help.
2. Added `remove transfer` command. See help.
3. Added `version` command.

## 0.0.1-SNAPSHOT
Initial version. See help for available commands.

# Local development
Copy /src/main/resources/koin.properties to /src/main/resources/koin-local.properties and fill properties with real values
Run `./gradlew run -i --args='/koin-local.properties'`