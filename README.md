# Staff_Id_Project

株式会社伊吉グループ向けのスタッフカードAPIです。  
スタッフ管理、店舗管理、クーポン利用ログ管理、ログイン機能を提供します。

## 構成概要

- **Backend**: Spring Boot 3.5.13 (Java 17)
- **Data Access**: MyBatis（アノテーションMapper）
- **DB**: PostgreSQL 16
- **Auth/Security**: Spring Security + BCrypt + JJWT
- **Build**: Maven Wrapper（`mvnw` / `mvnw.cmd`）

主要ディレクトリ:

- `src/main/java/.../controller`: REST API
- `src/main/java/.../service`: 業務ロジック
- `src/main/java/.../repository`: Repository層
- `src/main/java/.../mapper`: MyBatis SQL
- `src/main/java/.../auth`: ログイン処理
- `src/main/java/.../auth/config`: Security/JWT関連
- `initdb.d/01_schema.sql`: 初期スキーマ
- `docker-compose.yml`: DB起動定義

## ローカル起動

1. DB起動
   ```bash
   docker compose up -d
   ```
2. アプリ起動（Windows）
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

`application.properties` 既定値:

- DB URL: `jdbc:postgresql://localhost:5433/staff_db`
- DB User: `user`
- DB Password: `password`
- JWT Secret: `jwt.secret`（Base64）
- JWT Expiration: `jwt.expiration`（ミリ秒、既定 6000000）

## DBスキーマ

`initdb.d/01_schema.sql` で以下を作成:

- `stores`（店舗）
- `users`（スタッフ）
- `usage_logs`（利用ログ）

初期データとして `stores` に1件（`id=1`, `com_id='h001'`）が投入されます。

## API一覧

### 認証

- `POST /api/auth/login`
  - Request:
    ```json
    { "staffId": "s001", "password": "plain-password" }
    ```
  - 現在の実装:
    - `staff_id` でユーザー検索
    - `INACTIVE` は拒否
    - BCrypt照合
    - 成功時レスポンスは **文字列** `ログイン成功しました`
    - 失敗時は `401` + エラーメッセージ

### スタッフ管理（`/api/users`）

- `GET /api/users` スタッフ一覧
- `POST /api/users` スタッフ作成（受信パスワードをBCrypt化）
- `PUT /api/users/{id}/name` 名前変更
- `PUT /api/users/{id}/status?status=ACTIVE|INACTIVE` ステータス変更
- `PUT /api/users/{id}/passwordReset` パスワード再設定（BCrypt化）
- `PUT /api/users/{id}/role` ロール変更（`admin`, `manager`, `staff` のみ）

### 店舗管理（`/api/store`）

- `GET /api/store` 店舗一覧
- `POST /api/store` 店舗追加（`is_active=true`）
- `GET /api/store/{id}/all` 店舗情報 + 所属ユーザー一覧
- `PUT /api/store/{id}/active` 稼働状態更新（Boolean）

### 利用ログ（`/api/usage`）

- `POST /api/usage/use`
  - `userId`, `usedAtStoreId` を受け取り利用記録
  - 同一ユーザーは1日1回まで（2回目はエラー）
- `GET /api/usage/all`
  - 利用ログをユーザー名・店舗名付きで返却

## セキュリティ実装の現状

- `SecurityConfig` で `BCryptPasswordEncoder` をBean化
- CSRFは無効
- `/api/auth/**` は許可
- 現在は `anyRequest().permitAll()`（開発向け）
- JWT生成/検証クラス（`JwtService`, `JwtAuthenticationFilter`）は存在
- `AuthenticationService` はJWT付き `LoginResponse` を返せる実装あり  
  ただし現行 `AuthController` は `UserAuthService` を使い、レスポンスは成功メッセージ文字列

## テスト

```bash
.\mvnw.cmd test
```
