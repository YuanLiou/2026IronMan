# 教材勘誤

## Day 14

- 受影響 branch / commit：`day14/photo-owned` / `1e2152a`。
- 問題：照片 renderer 以完整限定名稱取得 Compose `LocalContext`，降低程式閱讀性。
- 影響：沒有行為、圖片、JSON 或教學流程差異。
- 修正：追加 correction commit，改為直接 import `LocalContext`。
- 學生動作：若要取得最新程式碼，請重新取得 `day14/photo-owned`；已下載版本仍可正常執行。

## Day 19

- 受影響 branch / commit：`day19/validation-extracted` / `1dceb9d`。
- 問題：原教材只以 Android instrumented test 驗證 `PhotoDiaryViewModel.saveDiary` 的 public behavior；`DiaryValidator` 已是 pure Kotlin collaborator，缺少可直接驗證其 own outcome 的補充教材狀態。
- 影響：原 branch 的 App 行為、既有 Android tests 與影片流程都正確，無需重新下載或修改；這是補充 Unit Test 學習範圍，不是產品 defect。
- 修正：保留原 branch，新增 `day19/validation-extracted-v2` / `253b5a4`，只加入 `DiaryValidatorTest.kt` 的三個 direct JVM Unit Tests。
- 學生動作：跟隨既有 Day 19 內容時繼續使用 `day19/validation-extracted`。要學習 direct Unit Test 時，執行 `git switch day19/validation-extracted-v2`；請先自行嘗試，不要立即開啟。
