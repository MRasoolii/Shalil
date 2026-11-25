# Shalil Android app

This is an offline-first Android Studio/Gradle project for the Shalil shop app.

## Build APK using GitHub Actions
1. Extract this ZIP.
2. Upload the complete contents (including the `.github` folder) to the root of your repository `MRasoolii/Shalil`.
3. Open the repository's **Actions** tab.
4. Select **Build Shalil APK** and press **Run workflow** (or push to `main`/`master`).
5. When the run finishes, open it and download the **Shalil-Android-APK** artifact.

## Included in this version
- Offline local storage for products and invoices
- Add and delete products (name, barcode, buying price, selling price, opening stock)
- Create sales invoices by entering a product name or barcode and quantity
- Save invoices and view invoice history
- Basic sales totals and invoice count
- Persian/English language toggle
- Rial/Toman toggle and light/dark toggle

## Important scope notes
This is a working first version of the source project, not the complete feature set originally discussed. Camera barcode scanning, Bluetooth barcode scanners, Bluetooth receipt printing (58/80 mm), photo attachments, detailed day/week/month/year reports, inventory movement, and import/export backup are not implemented yet. The current product flow accepts a barcode typed from a scanner that behaves like a keyboard. Test the APK on your phone before using it for real shop accounting.

The app uses Android Gradle Plugin 8.6.1, Gradle 8.7, and JDK 17.
