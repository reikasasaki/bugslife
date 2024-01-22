# TaxType 設計

## 概要

税額の管理を行う

### アクション

- 一覧
- 照会
- 作成
- 削除

### 要件

- 税額は ID、税率、入力価格（税抜き・税込み）、端数処理（切り捨て・四捨五入・切り上げ）を持つ
- 一覧ページ（登録・参照・削除）
  　表示項目は税率（％）と操作のみ
  　税率は重複表示しないこと
- 登録ページ
  　入力は税率（％）のみ　入力は数値とする
  　登録済みの税率と同じ数値の登録をさせないこと
  　入力された税率に従い、税込み/抜き × 丸目（Floor,Round,Ceil）の全 6 パターンのデータが作成されること
- 削除は使用している ID を削除できないこと

## モデル

- TaxType

| ID  | Rate(%) | Tax included | Rounding |
| --- | ------- | ------------ | -------- |
| 1   | 0       | No           | Floor    |
| 2   | 0       | No           | Round    |
| 3   | 0       | No           | Ceil     |
| 4   | 0       | Yes          | Floor    |
| 5   | 0       | Yes          | Round    |
| 6   | 0       | Yes          | Ceil     |
| 7   | 8       | No           | Floor    |
| 8   | 8       | No           | Round    |
| 9   | 8       | No           | Ceil     |
| 10  | 8       | Yes          | Floor    |
| 11  | 8       | Yes          | Round    |
| 12  | 8       | Yes          | Ceil     |
| 13  | 10      | No           | Floor    |
| 14  | 10      | No           | Round    |
| 15  | 10      | No           | Ceil     |
| 16  | 10      | Yes          | Floor    |
| 17  | 10      | Yes          | Round    |
| 18  | 10      | Yes          | Ceil     |
