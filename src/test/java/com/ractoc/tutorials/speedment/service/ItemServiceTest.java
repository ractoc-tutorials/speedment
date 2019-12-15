package com.ractoc.tutorials.speedment.service;

import ch.vorburger.exec.ManagedProcessException;
import com.ractoc.tutorials.speedment.db.SpeedmentApplicationBuilder;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.Item;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.ItemManager;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.generated.GeneratedItem;
import org.assertj.core.api.WithAssertions;
import org.dbunit.DatabaseUnitException;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.speedment.runtime.core.ApplicationBuilder.LogType.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test the ItemService")
class ItemServiceTest extends SpeedmentDBTestCase implements WithAssertions {

    public static final String DATASET_INITIAL = "datasets/initial.xml";
    public static final String DATASET_SAVE = "datasets/save.xml";
    public static final String DATASET_UPDATE = "datasets/update.xml";
    public static final String DATASET_DELETE = "datasets/delete.xml";
    public static final int PORT_NUMBER = 1234;
    public static final String DB_NAME = "my_collection";
    public static final String DB_PASSWORD = "MyCollection";
    public static final String SCHEMA_SCRIPT = "sql/create_schema.sql";
    public static final String TABLE_NAME = "item";
    public static final String ITEM_ID = "e00f7211-1982-40c1-ac96-b04c3a44adde";
    public static final String ITEM_NAME = "test name";
    public static final String ITEM_DESCRIPTION = "test description";
    public static final String CREATE_TEST = "create test";
    public static final String MORE_TESTS = "more tests";
    public static final String NEW_ITEM_ID = "f5d58333-0e06-4824-94f6-eb99995ea5dd";
    public static final String ITEM_LEAD = "base lead";
    public static final String UNKNOWN_NAME = "unknown name";
    public static final String UNKNOWN_ID = "unknown id";
    public static final String UNKNOWN_ITEM_ID = "e00f7211-1982-40c1-ac96-b04c3a44abcde";

    private ItemService service;

    private Item baseItem = ItemBuilder.builder().name(ITEM_NAME).lead(ITEM_DESCRIPTION).itemType(GeneratedItem.ItemType.BOARDGAMES).build();
    private Item updateItem = ItemBuilder.builder().id(ITEM_ID).name(ITEM_NAME).lead(ITEM_DESCRIPTION).itemType(GeneratedItem.ItemType.BOARDGAMES).build();

    @BeforeAll
    static void setupDatabase() throws ManagedProcessException {
        createDatabase(DB_NAME, PORT_NUMBER, SCHEMA_SCRIPT, DATASET_INITIAL);
    }

    @AfterAll
    static void tearDownAll() throws ManagedProcessException {
        destroyDatabase();
    }

    @BeforeEach
    void setUp() {
        ItemManager itemManager;
        itemManager = new SpeedmentApplicationBuilder()
                .withPassword(DB_PASSWORD)
                .withConnectionUrl(dbUrl)
                .withLogging(STREAM)
                .withLogging(REMOVE)
                .withLogging(PERSIST)
                .withLogging(UPDATE)
                .withParam("db.mysql.collationName", "utf8mb4_general_ci")
                .withParam("db.mysql.binaryCollationName", "utf8mb4_bin")
                .build().getOrThrow(ItemManager.class);
        service = new ItemService(itemManager);
    }

    @Test
    void getItemList() {
        // When
        Stream<Item> result = service.getItemList();

        // Then
        List<String> resultItems = result.map(Item::getName).collect(Collectors.toList());
        assertThat(resultItems).isNotEmpty();
        assertThat(resultItems).hasSize(2);
        assertThat(resultItems).containsExactlyInAnyOrder(CREATE_TEST, MORE_TESTS);
    }

    @Test
    void getItemByName() {
        // When
        Item result = service.getItemByName(CREATE_TEST);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ITEM_ID);
    }

    @Test
    void getItemByNameNoSuchEntry() {
        // When
        assertThrows(NoSuchEntryException.class, () -> service.getItemByName(UNKNOWN_NAME));
    }

    @Test
    void getItemById() {
        // When
        Item result = service.getItemById(ITEM_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(CREATE_TEST);
    }

    @Test
    void getItemByIdNoSuchEntry() {
        // When
        assertThrows(NoSuchEntryException.class, () -> service.getItemById(UNKNOWN_ID));
    }

    @Test
    void saveItem() throws Exception {
        // When
        Item result = service.saveItem(baseItem);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotIn(ITEM_ID, NEW_ITEM_ID);
        compareItemTable(DATASET_SAVE, TABLE_NAME);
    }

    @Test
    void saveItemDuplicateEntry() {
        // Given
        Item item = ItemBuilder.builder().name(CREATE_TEST).build();

        // When
        assertThrows(DuplicateEntryException.class, () -> service.saveItem(item));

        // Then
    }

    @Test
    void updateItem() throws Exception {
        // When
        Item result = service.updateItem(updateItem);

        // Then
        assertThat(result).isNotNull();
        compareItemTable(DATASET_UPDATE, TABLE_NAME);
    }

    @Test
    void updateItemSameNameSameItem() throws Exception {
        // Given
        Item item = ItemBuilder.builder().id(ITEM_ID).name(CREATE_TEST).lead(ITEM_LEAD).itemType(GeneratedItem.ItemType.BOARDGAMES).build();

        // When
        Item result = service.updateItem(item);

        // Then
        assertThat(result).isNotNull();
        compareItemTable(DATASET_INITIAL, TABLE_NAME);
    }

    @Test
    void updateItemDuplicateEntry() {
        // Given
        Item item = ItemBuilder.builder().id(ITEM_ID).name(MORE_TESTS).lead(ITEM_LEAD).build();

        // When
        assertThrows(DuplicateEntryException.class, () -> service.updateItem(item));

        // Then
    }

    @Test
    void updateItemNoSuchEntry() {
        // Given
        Item item = ItemBuilder.builder().id(UNKNOWN_ITEM_ID).name(MORE_TESTS).lead(ITEM_LEAD).build();

        // When
        assertThrows(NoSuchEntryException.class, () -> service.updateItem(item));
    }

    @Test
    void deleteItem() throws DatabaseUnitException, SQLException {
        // Test
        service.deleteItem(ITEM_ID);

        // Then
        compareItemTable(DATASET_DELETE, TABLE_NAME);
    }

    @Test
    void deleteItemUnknownId() throws DatabaseUnitException, SQLException {
        // Test
        service.deleteItem(ItemServiceTest.UNKNOWN_ITEM_ID);

        // Then
        compareItemTable(DATASET_INITIAL, TABLE_NAME);
    }
}
