# Simple Radio 📻

<p align="center">
  <img src="docs/images/app-icon.svg" width="128" alt="Simple Radio app icon">
</p>

[![Android CI](https://github.com/alex-amenos/Simple-Radio/actions/workflows/android_ci.yml/badge.svg?branch=main)](https://github.com/alex-amenos/Simple-Radio/actions/workflows/android_ci.yml)
![Kotlin](https://img.shields.io/badge/Kotlin-2.4.20-7F52FF?logo=kotlin&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-26-3DDC84?logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

Simple Radio is a lightweight internet radio app for Android. Search thousands of stations from the public [Radio Browser](https://www.radio-browser.info/) directory by name and country, save your favorites, and keep listening in the background with a sleep timer. It is built with modern Android practices: Kotlin, Jetpack Compose, an MVI presentation layer, Koin for dependency injection, and Arrow for functional error handling.

## 📸 Visuals

<table>
  <tr>
    <td align="center"><img src="docs/images/favorites-dark.png" width="260" alt="Favorites screen in dark theme"><br><sub>Favorites (dark theme)</sub></td>
    <td align="center"><img src="docs/images/favorites-light.png" width="260" alt="Favorites screen in light theme"><br><sub>Favorites (light theme)</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/images/search-default-country.png" width="260" alt="Search screen with the default country"><br><sub>Search with default country</sub></td>
    <td align="center"><img src="docs/images/search-by-name.png" width="260" alt="Search results for the word jazz"><br><sub>Search by station name</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/images/country-selector.png" width="260" alt="Country selector screen"><br><sub>Country selector</sub></td>
    <td align="center"><img src="docs/images/country-filter.png" width="260" alt="Country selector filtered by ital"><br><sub>Country search</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/images/search-by-country.png" width="260" alt="Search results for jazz in Italy"><br><sub>Results filtered by country</sub></td>
    <td align="center"><img src="docs/images/player.png" width="260" alt="Full-screen radio player"><br><sub>Full-screen player</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/images/mini-player.png" width="260" alt="Mini player bar above the navigation bar"><br><sub>Mini player bar</sub></td>
    <td align="center"><img src="docs/images/sleep-timer-menu.png" width="260" alt="Sleep timer options"><br><sub>Sleep timer options</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/images/sleep-timer-active.png" width="260" alt="Player with an active sleep timer"><br><sub>Active sleep timer</sub></td>
    <td align="center"><img src="docs/images/settings-dark.png" width="260" alt="Settings screen in dark theme"><br><sub>Settings (dark theme)</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/images/settings-light.png" width="260" alt="Settings screen in light theme"><br><sub>Settings (light theme)</sub></td>
    <td align="center"><img src="docs/images/media-notification.png" width="260" alt="Media notification playing LOS40 Classic with its controls"><br><sub>Media notification while playing</sub></td>
  </tr>
</table>

## ✨ Features

- **Search stations by name** ([Search by station name](docs/images/search-by-name.png)): type part of a station name and get matching live streams with their country, format and bitrate.
- **Filter by country** ([Country selector](docs/images/country-selector.png), [Country search](docs/images/country-filter.png), [Results by country](docs/images/search-by-country.png)): pick any country from the list, or search within it to find it faster.
- **Default country** ([Settings](docs/images/settings-dark.png)): choose the country that is preselected when you open the search.
- **Favorites** ([Favorites](docs/images/favorites-dark.png)): add or remove stations with the heart icon in search results, and manage them in a grid with their artwork. Remove a station with the trash icon on its card.
- **Live playback** ([Player](docs/images/player.png)): play, pause and stop a station, and move to the previous or next station. The screen shows a *Live* indicator while the stream is playing.
- **Background playback with mini player** ([Mini player](docs/images/mini-player.png)): leave the player and keep listening. A bar above the navigation shows the current station and lets you pause it.
- **Media notification** ([Media notification](docs/images/media-notification.png)): while a station plays, the notification shade shows its name and artwork, with pause/play and previous/next controls, so you can manage playback without opening the app.
- **Sleep timer** ([Sleep timer options](docs/images/sleep-timer-menu.png), [Active timer](docs/images/sleep-timer-active.png)): stop playback after 15, 30 or 60 minutes, with a countdown on the player.
- **Theme selection** ([Dark](docs/images/settings-dark.png), [Light](docs/images/settings-light.png)): choose light, dark or system theme. The change applies immediately.
- **Offline awareness**: a banner warns when there is no internet connection, so you know why a station will not load.

## 🧭 Use cases

- **Find a station in a specific country.** Open *Search*, pick the country in the dropdown and type a name or genre such as "jazz".
- **Discover stations from another country.** Use the country selector to search the list and switch the results to that country.
- **Build a personal list.** Tap the heart on the stations you like and open *Favorites* to listen to them later from one place.
- **Listen while doing something else.** Start a station, go back to browse, and keep the stream playing with the mini player or the media notification.
- **Fall asleep to the radio.** Set a 15, 30 or 60 minute sleep timer and let playback stop by itself.
- **Set your preferred look.** Use light, dark or system theme and a default country that matches where you usually listen.

## 📚 Documentation

All project documentation lives in the [`docs`](./docs) folder.

* **[Project Setup Guide](./docs/SETUP.md):** Step-by-step instructions for configuring and running the app.
* **[End-to-End Testing Guide](./docs/E2E_TESTING.md):** How the E2E test suite is structured, how to run it, and the screen robot pattern.
* **[GitHub Guide](./docs/github.md):** Repository, CI workflows and the GitHub Actions used by the project.
* **Architecture Decision Records:**
  * [ADR 0001: Structured Coroutines Lint Integration](./docs/adr/0001-structured-coroutines.md)
  * [ADR 0002: End-to-End Testing Strategy & Architecture](./docs/adr/0002-end-to-end-testing-strategy.md)
  * [ADR 0003: Reactive Theming, Connectivity Monitoring, and ViewModel Patterns](./docs/adr/0003-reactive-theming-and-connectivity.md)
  * [ADR 0004: Deriving UiState with `stateIn` in ViewModels](./docs/adr/0004-ui-state-with-statein.md)

## ⚖️ Disclaimer

Simple Radio does not host, store or retransmit any audio of its own. The app only plays streams that broadcasters publish and that are listed in the public Radio Browser directory. All rights to station names, logos and audio belong to each radio station or its rights holders. This project is not affiliated with, endorsed by or sponsored by any of them.

## 🔗 Useful Links for Development

- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Lint Rules by Slack](https://slackhq.github.io/compose-lints/rules/)
- [Ktlint Rules by Pinterest](https://pinterest.github.io/ktlint/rules/standard/)
- [AndroidX Libraries Versions](https://developer.android.com/jetpack/androidx/versions)
- [Compose BOM Version Mapping](https://developer.android.com/jetpack/compose/bom/bom-mapping)
- [Jetpack Compose to Kotlin Compatibility Map](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
- [Google App Architecture](https://developer.android.com/topic/architecture/intro)
