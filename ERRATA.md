# 教材勘誤

## Day 14

- 受影響 branch / commit：`day14/photo-owned` / `1e2152a`。
- 問題：照片 renderer 以完整限定名稱取得 Compose `LocalContext`，降低程式閱讀性。
- 影響：沒有行為、圖片、JSON 或教學流程差異。
- 修正：追加 correction commit，改為直接 import `LocalContext`。
- 學生動作：若要取得最新程式碼，請重新取得 `day14/photo-owned`；已下載版本仍可正常執行。
