# TaskManage

TaskManage is the cross-platform Vue 3 migration of the original Android TaskMate project. The Java app remains in the parent directory as a behaviour reference; this folder is the new uni-app application.

## Stack

- uni-app (Vue 3)
- JavaScript first; TypeScript can be introduced after the feature baseline is stable
- Pinia for session and task state
- uni storage for the local-only prototype
- Platform adapters under `src/native/` for torch and reminders

## Run

```bash
npm install
npm run dev:h5
```

For device previews, open the project in HBuilderX and run it to the browser, an Android device/emulator, an iOS simulator/device, or the relevant mini-program developer tool.

## What is migrated

- Login and local registration flow
- Per-user task storage
- Task list with explicit completion, edit and delete actions
- Add/edit form with due date and progress preservation
- Local reminder list
- Camera preview page and Android torch adapter scaffold

The local account is intentionally marked as prototype-only. Do not use it for real credentials. Before release, move authentication to a backend and replace the platform reminder adapter with a tested local-notification plugin for Android and iOS.

## Native capability notes

The browser build displays capability placeholders. Camera preview requires a device or a platform simulator. Torch control currently includes an Android App adapter; iOS needs a native plugin. Reminder scheduling uses the App runtime's local message API where available, but cancellation and exact background behaviour still require a production notification plugin and device testing.
