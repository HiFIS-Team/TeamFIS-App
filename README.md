# TeamFIS-App

[ TeamFISㅣApp ] 피트니스스타 트레이너를 TeamFIS App 하나로

Kotlin Multiplatform (KMP) 기반 모바일 앱입니다. 비즈니스 로직은 `shared` 모듈에 두고,
UI는 Android(Jetpack Compose)와 iOS(SwiftUI) 각각 네이티브로 구현합니다.

## 프로젝트 구조

```
TeamFIS-App/
├── shared/          # Kotlin Multiplatform 공용 모듈 (Android + iOS)
│   └── src/
│       ├── commonMain/    # 공통 코드
│       ├── androidMain/   # Android 전용 actual 구현
│       ├── iosMain/       # iOS 전용 actual 구현
│       └── commonTest/    # 공통 테스트
├── androidApp/      # Android 앱 (Jetpack Compose)
├── iosApp/          # iOS 앱 (SwiftUI, Xcode 프로젝트)
└── gradle/
    └── libs.versions.toml   # 버전 카탈로그 (모든 의존성 버전은 여기서 관리)
```

`shared` 모듈은 iOS 쪽에 `SharedKit`이라는 이름의 static framework로 노출됩니다.
Swift에서는 `import SharedKit` 으로 사용합니다.

## 요구 사항

| 도구 | 버전 |
|------|------|
| JDK | 17 이상 |
| Android SDK | Platform 37 (Android 17), Build-Tools 37.0.0 |
| Xcode | 26 이상 (iOS 빌드 시, macOS 전용) |
| Gradle | Wrapper 사용 (9.7.1, 별도 설치 불필요) |

## 초기 설정

1. 저장소 클론

   ```bash
   git clone <repo-url>
   cd TeamFIS-App
   ```

2. `local.properties` 생성 (버전 관리에서 제외되어 있어 클론 후 직접 만들어야 합니다)

   ```bash
   echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties
   ```

   Homebrew의 `android-commandlinetools`를 쓰는 경우:

   ```bash
   echo "sdk.dir=/opt/homebrew/share/android-commandlinetools" > local.properties
   ```

3. `JAVA_HOME` 설정 (터미널에서 빌드할 때 필요)

   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   ```

   Homebrew의 `openjdk@17`을 쓰는 경우:

   ```bash
   export JAVA_HOME=/opt/homebrew/opt/openjdk@17
   ```

## 빌드 & 실행

### Android

```bash
# 디버그 APK 빌드 → androidApp/build/outputs/apk/debug/
./gradlew :androidApp:assembleDebug

# 연결된 기기/에뮬레이터에 설치
./gradlew :androidApp:installDebug

# 설치 후 실행
adb shell am start -n com.teamfis.app/.MainActivity

# 릴리즈 APK
./gradlew :androidApp:assembleRelease
```

에뮬레이터 실행:

```bash
emulator -avd <AVD_NAME>   # 사용 가능한 목록: emulator -list-avds
```

### iOS

Xcode에서 `iosApp/iosApp.xcodeproj`를 열고 실행(⌘R)하면 됩니다.
빌드 시 `Build Kotlin/Native framework` 스크립트 단계가 자동으로
`./gradlew :shared:embedAndSignAppleFrameworkForXcode`를 실행해 `SharedKit`을 만들어 넣습니다.

커맨드라인으로 빌드하려면:

```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
  -derivedDataPath iosApp/build \
  build
```

시뮬레이터에 설치/실행:

```bash
xcrun simctl install booted iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app
xcrun simctl launch booted com.teamfis.app
```

`shared` 프레임워크만 따로 빌드하려면:

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

## 테스트

```bash
# 공용 모듈 전체 테스트 (JVM/Android + iOS)
./gradlew :shared:allTests

# 전체 빌드 + 검증 (lint 포함)
./gradlew build
```

## 기술 스택

- **Kotlin** 2.4.10 (Multiplatform)
- **Gradle** 9.7.1 (Wrapper, Kotlin DSL, 버전 카탈로그)
- **Android Gradle Plugin** 9.3.1
- **Android**: minSdk 24 / targetSdk 37 / compileSdk 37, Jetpack Compose (BOM 2026.08.00), Material 3
- **iOS**: SwiftUI, deployment target 17.0, 타겟 `iosArm64` / `iosSimulatorArm64` / `iosX64`
- **Application ID / Bundle ID**: `com.teamfis.app`

의존성 버전은 모두 [gradle/libs.versions.toml](gradle/libs.versions.toml)에서 관리합니다.

## 브랜드 자산

앱 아이콘은 **원본 하나(`assets/brand/logo.png`, 1254×1254, 검정 배경 위 붉은 FS 마크)에서 구워 씁니다.**
아이콘 파일을 손으로 고치지 말고, 원본을 갈아 끼운 뒤 스크립트를 다시 돌리세요.

```bash
python3 tools/icons/gen_app_icon.py            # assets/brand/logo.png 사용
python3 tools/icons/gen_app_icon.py 다른원본.png  # 원본을 직접 지정
```

굽는 곳:

| 결과물 | 위치 |
|--------|------|
| Android 레거시(API 25 이하) | `androidApp/src/main/res/mipmap-*/ic_launcher.png` · `ic_launcher_round.png` |
| Android 어댑티브(API 26+) | `mipmap-*/ic_launcher_foreground.png` · `ic_launcher_monochrome.png` + `mipmap-anydpi-v26/*.xml` (배경은 `@color/ic_launcher_background`) |
| iOS | `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/AppIcon.png` (1024, 불투명 — iOS는 알파를 못 씁니다) |
| 마크만 필요한 곳 | `assets/brand/logo-mark.png` (배경 없는 벌, 같이 구워집니다) |

## 아직 안 된 것

- 디자인 시스템 (`.claude/DESIGN.md`) · 기능 명세 (`.claude/SPEC.md`)
