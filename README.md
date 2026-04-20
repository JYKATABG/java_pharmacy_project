How to run this project

### 1. Clone the repository
```
git clone <url>
```

### 2. Download the required library and place it in the `lib/` folder

| Library | Download |
|---|---|
| H2 Database | https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar |

### 3. Run the project

Open the project in VS Code or IntelliJ and run `App.java`

> The database is created automatically on every startup — no additional configuration needed.

Pharmacy Management System — Quick Ref

1. Накратко

Стек: Java 21, Swing (UI), H2 (In-memory DB).
Слоеве: UI (Panels) → DAO (Data Access) → Database (H2).
База: Данните се рестартират при всяко пускане (In-memory). 2. Архитектура и Данни

Проектът използва 3-слойна архитектура.
Модели (POJOs):

    Medication: име, производител, цена, наличност, категория.

    Client: име, фамилия, телефон, дата на раждане.

    Sale: връзка клиент-лекарство, количество, обща цена, начин на плащане.

База данни (Schema):

    categories 1:N medications

    medications 1:N sales

    clients 1:N sales

3. Логика на работа (DAO & UI)
   DAO Патерн:

Всеки DAO клас (MedicationDAO, ClientDAO, SaleDAO) капсулира SQL заявките:

    getAll(): Извлича всички записи (използва JOIN за имена на категории/клиенти).

    insert() / update(): Използват PreparedStatement против SQL инжекция.

    delete(): Премахва запис по ID.

UI Механика:

Всички панели (MedicationPanel, ClientPanel, SalePanel) споделят еднаква логика:

    Търсене: Филтрира JTable чрез SQL LIKE заявка.

    Селекция: При клик върху ред в таблицата, обектът се зарежда във формата и се запазва неговото selectedId.

    Запис: * Ако selectedId == -1 → DAO.insert().

        Ако selectedId > 0 → DAO.update().

4. Конфигурация и Стартиране

   Библиотеки: Изисква се h2-x.x.x.jar в папка lib/.

   DB Връзка: jdbc:h2:mem:pharmacydb;DB_CLOSE_DELAY=-1

   Автоматизация: DatabaseManager изпълнява schema.sql автоматично при старт.

Таб Основно действие
Лекарства Управление на наличности и категории.
Клиенти Търсене по фамилия; формат на дата: yyyy-MM-dd.
Продажби Избор на клиент/стока от падащи менюта (JComboBox).
