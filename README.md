### Clean Architecture + Layered Architecture

# 1. 核心概念：同心円構造

Clean Architectureでは、**内側に行くほど重要なビジネスロジック**が配置されます。

```
外側 → 内側の方向：
Presentation → Infrastructure → Application → Domain（核心）

```

**核心ルール：**

- 外側のレイヤーは内側のレイヤーを知ることができる
- 内側のレイヤーは外側のレイヤーを絶対に知ってはいけない
- 依存性の矢印は常に内側（Domain）を向く

---

## 2. 各レイヤーの役割

### 🎯 Domain Layer（最も内側 - 核心）

**配置：** `domain/lecture/`、`domain/enrollment/`

**役割：**

- 純粋なビジネスルールのみを担当
- 「講座は30名まで申し込み可能」といった核心ルール

**特徴：**

- Spring、JPA、DBなどいかなる技術も知らない
- 純粋なJavaオブジェクトのみを使用
- 外部依存性が全くない

**含まれるファイル：**

- `Lecture.java` - 講座という概念そのもの
- `LectureRepository.java` - インターフェース（実装は知らない）

**なぜRepositoryインターフェースがここに？**

- Domainが「このようにデータを取得する」と要件のみを定義
- 実際の実装（MySQL？MongoDB？）はInfrastructureが担当
- これが**依存性逆転の原則（DIP）**

---

### 🎯 Application Layer（中間）

**配置：** `application/enrollment/`、`application/lecture/`

**役割：**

- ユースケース実装
- 「ユーザーが講座を申し込む」という全体フローの調整
- 複数のDomainオブジェクトを組み合わせて業務シナリオを完成

**特徴：**

- Domainのみに依存（Infrastructure、Presentationは知らない）
- トランザクション管理
- ビジネスフローのオーケストレーター

**含まれるファイル：**

- `EnrollmentService.java` - 講座申し込みシナリオ
- `LectureService.java` - 講座照会シナリオ
- `dto/` - レイヤー間のデータ転送オブジェクト

**なぜDTOを使うのか？**

- 各レイヤーを隔離するため
- Controllerが変わってもServiceは影響を受けない
- GraphQLに変更してもServiceコードは不変

---

### 🎯 Infrastructure Layer（外側）

**配置：** `infrastructure/persistence/`

**役割：**

- 実際の技術実装
- DB保存、外部API呼び出しなど
- DomainのRepositoryインターフェースを実際に実装

**特徴：**

- JPA、Hibernate、MySQLなど具体的な技術を使用
- Domainインターフェースの実装クラス
- EntityとDomainオブジェクトの変換を担当

**含まれるファイル：**

- `LectureEntity.java` - JPA用テーブルマッピングオブジェクト
- `LectureJpaRepository.java` - Spring Data JPA
- `LectureRepositoryImpl.java` - DomainのRepository実装

**なぜEntityとDomainを分離？**

- Entity：DB保存方式（技術的関心事）
- Domain：ビジネスルール（業務的関心事）
- MySQL → MongoDBに変更してもDomainコードは変わらない

---

### 🎯 Presentation Layer（最も外側）

**配置：** `presentation/api/`

**役割：**

- 外部世界との接点
- HTTPリクエスト/レスポンス処理
- REST API、GraphQL、CLIなど

**特徴：**

- Application Layerのみを呼び出す
- DomainやInfrastructureへの直接アクセス禁止
- HTTP関連コードのみが存在

**含まれるファイル：**

- `EnrollmentController.java` - REST APIエンドポイント
- `request/` - HTTPリクエストオブジェクト
- `exception/` - エラーレスポンス処理

---

## 3. 依存性のフロー（最も重要！）

### 従来方式の問題点

```
Controller → Service → RepositoryImpl（具象クラス）

```

**問題：**

- ServiceがMySQL JPAの実装に直接依存
- DBを変更するならServiceコードも修正が必要
- テスト時に実際のDBが必要

### Clean Architectureの解決策

```
Controller → Service → Repository（インターフェース）
                          ↑
                    RepositoryImpl（実装クラス）

```

**メリット：**

- Serviceはインターフェースのみを知れば良い
- DBを変更してもServiceコードは不変
- Mockオブジェクトで容易にテスト可能

---

## 4. 実際のフロー例：講座申し込み

### ステップ1：ユーザーが「講座申し込み」をクリック

**Presentation（Controller）**

- HTTPリクエストを受信
- JSON → DTO変換
- Application Layerに渡す

### ステップ2：業務フローの開始

**Application（Service）**

- 「講座申し込み」ユースケースを実行
- 順序：
    1. 重複申し込みチェック
    2. 講座情報照会
    3. Domainオブジェクトにビジネスルールの実行を要求
    4. 結果を保存

### ステップ3：ビジネスルールの検証

**Domain（Lecture）**

- 「現在の申込者が30名未満か？」をチェック
- ルール違反時は例外を発生
- ルールをパスしたら申込者数を増加

### ステップ4：データ保存

**Infrastructure（RepositoryImpl）**

- DomainオブジェクトをEntityに変換
- JPAでDBに保存
- EntityをDomainオブジェクトに再変換

### ステップ5：レスポンスを返す

**Presentation（Controller）**

- 結果をJSONに変換
- HTTPレスポンスを送信

---

## 5. なぜこんなに複雑に分けるのか？

### メリット1：ビジネスロジックの保護

- Domainは純粋なJava
- DB、Frameworkが変わってもビジネスルールは不変
- 「30名制限」ルールが技術変更の影響を受けない

### メリット2：テスト容易性

- Domain：newキーワードのみでテスト可能
- Application：MockオブジェクトでDB不要でテスト
- Infrastructure：実際のDBでのみテスト

### メリット3：変更影響の最小化

- REST API → GraphQL変更：Presentationのみ修正
- MySQL → MongoDB変更：Infrastructureのみ修正
- ビジネスルール変更：Domainのみ修正

### メリット4：明確な責任分離

- 各レイヤーが一つの責任のみを担当
- コードの場所を見つけやすい
- 複数の開発者が同時作業可能

---

## 6. 各レイヤーの独立性

### Domainの独立性

- Springなしでも動作
- DBなしでも動作
- 純粋なJavaでテスト可能
- **核心ビジネスルールのみに集中**

### Applicationの独立性

- HTTPプロトコルを知らなくても良い
- DB実装方式を知らなくても良い
- **業務フローのみに集中**

### Infrastructureの交換可能性

- MySQL → PostgreSQL交換可能
- JPA → MyBatis交換可能
- Domain、Applicationコードは不変

### Presentationの交換可能性

- REST → GraphQL交換可能
- Web → CLI交換可能
- Applicationコードは不変

---

## 7. 核心まとめ

### レイヤー別一行要約

- **Domain：** 「我が社の核心業務ルール」
- **Application：** 「ユーザーが行いたいことの全体フロー」
- **Infrastructure：** 「実際にDBに保存する方法」
- **Presentation：** 「ユーザーと対話する方法」

### 依存性方向の原則

```
Domainは技術的なこと（外部世界）を知らない

```

### **✅ Domainが知っていること（ビジネスルール）**

**会社の核心業務を完璧に把握：**

```
Domainの知識：
- 「講座は30名まで申し込み可能」
- 「同じ人は同じ講座に二度申し込めない」
- 「締め切られた講座はもう申し込み不可」
- 「講座の日付は過去になれない」

```

**これがまさに「ビジネスルール」であり、Domainの知識**


### Clean Architectureの目標

1. **ビジネスロジックを技術から独立**させる
2. **テストしやすく**する
3. **変更に柔軟に**対応する
4. **各レイヤーの責任を明確に**する

### 実務での価値

- 初期構造の構築に時間がかかる
- しかしプロジェクトが大きくなるほど管理が容易
- 技術的負債の最小化
- 新規開発者のオンボーディングが速い（構造が明確なため）

---

## 8. まとめ

Clean Architectureは**「ビジネスロジックを守る城壁」**のようなものです。

- 最も内側（Domain）に最も重要なものを配置
- 外側（Infrastructure、Presentation）はいつでも交換可能
- 依存性は常に内側へ
- 各レイヤーは自分の役割のみに集中
