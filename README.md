# Staff_Id_Project

株式会社伊吉グループ向けのスタッフカードAPIプロジェクトです。  
Spring Boot + MyBatis + PostgreSQL を使い、スタッフ管理・店舗管理・クーポン利用ログ管理・ログインを提供します。

## 技術スタック

- Java 17
- Spring Boot 3.5.13
- Spring Web / Spring Security / Spring JDBC
- MyBatis 3.0.5
- PostgreSQL 16（Docker利用）
- Maven Wrapper（`mvnw.cmd`）
- Lombok
- JJWT（依存追加済み、実運用トークン生成処理は未実装）

## 起動・DB構成

- DBは `docker-compose.yml` で `postgres:16-alpine` を起動
- 接続先は `localhost:5433/staff_db`
- 初期スキーマは `initdb.d/01_schema.sql` で投入

### 主要テーブル

- `stores`: 店舗マスタ（`com_id`, `com_name`, `location`, `is_active`）
- `users`: スタッフマスタ（`staff_id`, `password`, `name`, `role`, `store_id`, `status`）
- `usage_logs`: 利用ログ（`user_id`, `used_at_store_id`, `used_at`）

## 認証・セキュリティ機能

### ログインAPI

- `POST /api/auth/login`
- 入力: `staffId`, `password`
- 処理:
  - `staff_id` でユーザー検索
  - `status=INACTIVE` はログイン不可
  - BCryptでパスワード照合
- 返却:
  - 成功: `ログイン成功しました`
  - 失敗: `401 Unauthorized`

### セキュリティ設定

- `BCryptPasswordEncoder` をBean定義
- CSRFは無効化
- `/api/auth/**` は許可
- 現在は開発用設定で **全API許可（`anyRequest().permitAll()`）**

## 提供API機能

### 1. スタッフ管理（`/api/users`）

- `GET /api/users`  
  全スタッフ一覧取得
- `POST /api/users`  
  スタッフ新規登録（受け取った平文パスワードをBCryptで保存）
- `PUT /api/users/{id}/name`  
  名前変更
- `PUT /api/users/{id}/status?status=...`  
  ステータス変更（例: ACTIVE/INACTIVE）
- `PUT /api/users/{id}/passwordReset`  
  パスワードリセット（再ハッシュ化して更新）
- `PUT /api/users/{id}/role`  
  ロール変更（`admin` / `manager` / `staff` のみ許可）

### 2. 店舗管理（`/api/store`）

- `GET /api/store`  
  店舗一覧取得
- `POST /api/store`  
  店舗追加（`is_active=true` で登録）
- `GET /api/store/{id}/all`  
  指定店舗 + 所属ユーザー一覧取得
- `PUT /api/store/{id}/active`  
  店舗稼働ステータス更新（Boolean）

### 3. クーポン利用管理（`/api/usage`）

- `POST /api/usage/use`  
  クーポン利用を記録
  - 1ユーザーあたり1日1回まで（同日2回目はエラー）
- `GET /api/usage/all`  
  利用ログ一覧を取得（ユーザー名・店舗名をJOINして返却）

## レイヤー構成

- `controller`: APIエンドポイント
- `service`: 業務ルール（例: 1日1回制限、ロールバリデーション）
- `repository`: 永続化の仲介
- `mapper`: MyBatisのSQL定義
- `auth`: ログイン関連処理
- `dto`: API入出力モデル

## 現在の実装状態メモ

- JWT関連ライブラリは導入済みだが、トークン発行/検証ロジックは未実装
- `JwtUtils` は雛形のみ
- `application.properties` の JWT 設定はコメントアウト中
