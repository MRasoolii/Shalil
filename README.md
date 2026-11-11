# نرم افزار شلیل | Shalil

Android shop-management starter project. Java + SQLite, Persian/English UI, product CRUD, product image URI, invoice creation/history, invoice delete/edit workflow, USB keyboard-wedge barcode input and camera barcode scanning (Google Code Scanner).

## Build APK
- Open this folder in Android Studio (Hedgehog or newer) and sync Gradle.
- Build > Build Bundle(s) / APK(s) > Build APK(s).
- Or push the project to GitHub and run the `Build Shalil APK` workflow. Download the `Shalil-debug-apk` artifact from Actions.

## Barcode scanner
- USB/Bluetooth scanners that behave as a keyboard: focus the barcode field, scan, and press Enter (configure scanner suffix as Enter).
- Camera scanning uses Google Code Scanner and may download its module on first use; Google Play services is required.

## Notes
- Data is stored locally in `shalil.db` on the device; uninstalling the app removes local data. Export/backup and stock ledger are planned follow-up features.
- Product images are stored as persisted content URIs, not copied into a separate image archive.
- Invoice editing currently reopens saved items as a new editable draft after removing the old invoice. Review this workflow before using for real accounting.
