## 1.2.3 [19-06-2020]

- Improve memory leak detection and base class memory management

## 1.1.1

- Add `onArgumensReceived` for all viewModels to get arguments from dialogs, fragments and intents of activities when created

## 1.1.0

- Clean up base classes for activities and fragments
- Add BaseDialogFragment class to distinguish between dialogs and normal fragments
- BaseDialogFragments can bee in dialog or bottom sheet mode
- Dialogs can bear a result and fragment showing the dialogs can receive them
- Add a better system for consuming back presses. This works in fragments and dialogs
- Bump of all major versions of dependencies
- Bump to compile agains api 29

## 1.0.0

- Initial release