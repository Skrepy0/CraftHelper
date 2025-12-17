package com.crafthelper.client.gui.screen;

import com.crafthelper.item.ModItemTags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 分类
enum ItemType {
    All, BuildingBlocks, ColoredBlocks, NaturalBlocks, FunctionalBlocks, RedStoneBlocks,
    ToolsUtilities, Combat, FoodDrinks, Ingredients, SpawnEggs, OperatorUtilities, Others
};

public class CustomItemScreen extends Screen {

    // 物品分类相关
    private final Map<ItemType, List<Item>> itemsByCategory = new HashMap<>();
    private final List<ItemType> categories = new ArrayList<>();
    private List<Item> currentItems = new ArrayList<>();
    private ItemType currentCategory = ItemType.All;

    // 分页控制
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 45;
    private static final int ITEMS_PER_ROW = 9;

    // UI控件
    private TextFieldWidget searchField;
    private ButtonWidget prevPageButton;
    private ButtonWidget nextPageButton;
    private final List<ButtonWidget> categoryButtons = new ArrayList<>();

    // 选中状态
    private static Item selectedItem = null;

    // 界面尺寸 - 现在使用相对值
    private static final int MIN_GUI_WIDTH = 420;
    private static final int MIN_GUI_HEIGHT = 300;
    private static final int PREFERRED_GUI_WIDTH = 450;
    private static final int PREFERRED_GUI_HEIGHT = 340;

    // 实际GUI尺寸（根据窗口动态计算）
    private int guiWidth;
    private int guiHeight;
    private int guiLeft;
    private int guiTop;

    // 边距和间距（相对于GUI尺寸的比例）
    private final float MARGIN_RATIO = 0.02f; // 2%边距
    private final float CATEGORY_BUTTON_HEIGHT_RATIO = 0.06f; // 分类按钮高度
    private final float ITEM_GRID_TOP_RATIO = 0.425f; // 物品网格顶部位置
    private final float BOTTOM_BUTTON_AREA_HEIGHT_RATIO = 0.20f; // 底部按钮区域高度

    private ButtonWidget doneButton = null;
    // 关键：控制是否绘制模糊背景的标志
    private boolean shouldRenderBackground = false;

    public CustomItemScreen() {
        super(Text.literal("Item Selector"));
        initializeCategories();
    }

    /**
     * 初始化物品分类
     */
    private void initializeCategories() {
        // 添加分类（按常用度排序）
        categories.add(ItemType.All);
        categories.add(ItemType.BuildingBlocks);
        categories.add(ItemType.ColoredBlocks);
        categories.add(ItemType.NaturalBlocks);
        categories.add(ItemType.FunctionalBlocks);
        categories.add(ItemType.RedStoneBlocks);
        categories.add(ItemType.ToolsUtilities);
        categories.add(ItemType.Combat);
        categories.add(ItemType.FoodDrinks);
        categories.add(ItemType.Ingredients);
        categories.add(ItemType.SpawnEggs);
        categories.add(ItemType.OperatorUtilities);
        categories.add(ItemType.Others);

        // 初始化分类列表
        for (ItemType category : categories) {
            itemsByCategory.put(category, new ArrayList<>());
        }

        // 分类所有物品
        Registries.ITEM.forEach(item -> {
            if (item != Items.AIR) {
                ItemType itemType = categorizeItem(item);
                itemsByCategory.get(itemType).add(item);
                itemsByCategory.get(ItemType.All).add(item);
                if (itemType == ItemType.Others) {
                    System.out.println(Text.of(Registries.ITEM.getId(item)));
                }
            }
        });

        itemsByCategory.get(ItemType.All).add(Items.LIGHT);
        itemsByCategory.get(ItemType.All).add(Items.JIGSAW);
        itemsByCategory.get(ItemType.All).add(Items.STRUCTURE_BLOCK);
        itemsByCategory.get(ItemType.All).add(Items.BARRIER);
        itemsByCategory.get(ItemType.All).add(Items.STRUCTURE_VOID);

        itemsByCategory.get(ItemType.OperatorUtilities).add(Items.LIGHT);
        itemsByCategory.get(ItemType.OperatorUtilities).add(Items.JIGSAW);
        itemsByCategory.get(ItemType.OperatorUtilities).add(Items.STRUCTURE_BLOCK);
        itemsByCategory.get(ItemType.OperatorUtilities).add(Items.BARRIER);
        itemsByCategory.get(ItemType.OperatorUtilities).add(Items.STRUCTURE_VOID);

        currentItems = itemsByCategory.get(currentCategory);
    }

    /**
     * 计算GUI尺寸和位置
     */
    private void calculateGuiDimensions() {
        // 计算GUI宽度（考虑最小值和窗口大小）
        guiWidth = Math.max(MIN_GUI_WIDTH, Math.min(PREFERRED_GUI_WIDTH, (int) (width * 0.8f)));
        guiHeight = Math.max(MIN_GUI_HEIGHT, Math.min(PREFERRED_GUI_HEIGHT, (int) (height * 0.8f)));

        // 确保GUI不会超出窗口
        guiWidth = Math.min(guiWidth, width - 20);
        guiHeight = Math.min(guiHeight, height - 20);

        // 居中计算
        guiLeft = (width - guiWidth) / 2;
        guiTop = (height - guiHeight) / 2;
    }

    /**
     * 计算基于比例的像素值
     */
    private int scaledX(float ratio) {
        return guiLeft + (int) (guiWidth * ratio);
    }

    private int scaledY(float ratio) {
        return guiTop + (int) (guiHeight * ratio);
    }

    private int scaledWidth(float ratio) {
        return (int) (guiWidth * ratio);
    }

    private int scaledHeight(float ratio) {
        return (int) (guiHeight * ratio);
    }

    /**
     * 物品分类逻辑
     */
    private ItemType categorizeItem(Item item) {
        ItemStack stack = new ItemStack(item);

        // === 重要：调整判断顺序，从具体到一般 ===

        // 刷怪蛋（最具体，只有特定物品）
        if (item instanceof net.minecraft.item.SpawnEggItem) {
            return ItemType.SpawnEggs;
        }

        // 战斗用品（武器、盔甲、弹药）
        if (stack.isIn(ModItemTags.COMBAT)) {
            return ItemType.Combat;
        }

        // 食物与饮品（包括动物食物）
        if (stack.isIn(ModItemTags.FOOD_DRINKS) || stack.get(DataComponentTypes.FOOD) != null) {
            return ItemType.FoodDrinks;
        }

        // 工具与实用物品
        if (stack.isIn(ModItemTags.TOOLS_UTILITIES)) {
            return ItemType.ToolsUtilities;
        }

        // 红石相关物品
        if (stack.isIn(ModItemTags.REDSTONE_BLOCKS)) {
            return ItemType.RedStoneBlocks;
        }

        // 染色方块（包括染料相关）
        if (stack.isIn(ModItemTags.COLOR_BLOCKS)) {
            return ItemType.ColoredBlocks;
        }

        // 功能方块
        if (stack.isIn(ModItemTags.FUNCTIONAL_BLOCKS)) {
            return ItemType.FunctionalBlocks;
        }

        // 自然方块（基础材料和植物）
        if (stack.isIn(ModItemTags.NATURAL_BLOCKS)) {
            return ItemType.NaturalBlocks;
        }

        // 建筑方块（加工过的方块）
        if (stack.isIn(ModItemTags.BUILDING_BLOCKS)) {
            return ItemType.BuildingBlocks;
        }

        // 原材料（加工材料）
        if (stack.isIn(ModItemTags.INGREDIENTS)) {
            return ItemType.Ingredients;
        }

        // 生物蛋
        if (stack.isIn(ModItemTags.SPAWN_EGGS)) {
            return ItemType.SpawnEggs;
        }

        // 管理员物品
        if (stack.isIn(ModItemTags.OPERATOR_UTILITIES)) {
            return ItemType.OperatorUtilities;
        }

        // 未分类物品归为其他
        return ItemType.Others;
    }

    @Override
    protected void init() {
        super.init();

        // 计算GUI尺寸和位置
        calculateGuiDimensions();

        // 清除现有控件
        this.clearChildren();
        categoryButtons.clear();

        // 初始化搜索框
        initSearchField();

        // 初始化分类按钮
        initCategoryButtons();

        // 初始化分页按钮
        initPaginationButtons();

        // 初始化功能按钮（关闭、给予、批量）
        initFunctionButtons();
    }

    /**
     * 当窗口大小变化时重新初始化
     */
    @Override
    public void resize(net.minecraft.client.MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        // 重新初始化以调整布局
        this.init();
    }

    /**
     * 初始化搜索框
     */
    private void initSearchField() {
        int margin = scaledWidth(MARGIN_RATIO);
        int fieldWidth = scaledWidth(0.96f); // 96%宽度
        int fieldHeight = scaledHeight(0.060f); // 6%高度

        searchField = new TextFieldWidget(
                this.textRenderer,
                scaledX(MARGIN_RATIO),
                scaledY(MARGIN_RATIO * 3),
                fieldWidth,
                fieldHeight,
                Text.literal("Search Item")
        );
        searchField.setPlaceholder(Text.translatable("screen.custom_item.select_item.text_field"));
        searchField.setChangedListener(text -> {
            filterItems(text);
            currentPage = 0;
        });
        this.addDrawableChild(searchField);
    }

    /**
     * 绘制GUI背景（半透明面板 + 边框）
     */
    private void drawGuiBackground(DrawContext context) {
        // 绘制主面板（半透明黑色背景 + 白色边框）
        context.fill(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xAA000000);
        context.drawBorder(guiLeft, guiTop, guiWidth, guiHeight, 0xFFFFFFFF);
    }

    private Text translateKey(ItemType itemType) {
        return switch (itemType) {
            case All -> Text.translatable("screen.custom_item.select_item.item_type.all");
            case BuildingBlocks -> Text.translatable("itemGroup.buildingBlocks");
            case ColoredBlocks -> Text.translatable("itemGroup.coloredBlocks");
            case NaturalBlocks -> Text.translatable("itemGroup.natural");
            case FunctionalBlocks -> Text.translatable("itemGroup.functional");
            case RedStoneBlocks -> Text.translatable("itemGroup.redstone");
            case ToolsUtilities -> Text.translatable("itemGroup.tools");
            case Combat -> Text.translatable("itemGroup.combat");
            case FoodDrinks -> Text.translatable("itemGroup.foodAndDrink");
            case Ingredients -> Text.translatable("itemGroup.ingredients");
            case SpawnEggs -> Text.translatable("itemGroup.spawnEggs");
            case OperatorUtilities -> Text.translatable("itemGroup.op");
            case Others -> Text.translatable("screen.custom_item.select_item.item_type.others");
        };
    }

    /**
     * 初始化分类按钮
     */
    private void initCategoryButtons() {
        int buttonX = scaledX(MARGIN_RATIO);
        int buttonY = scaledY(0.15f);
        int buttonWidth = scaledWidth(0.18f);
        int buttonHeight = scaledHeight(CATEGORY_BUTTON_HEIGHT_RATIO);
        int gap = scaledWidth(0.015f); // 1.5%间距

        categoryButtons.clear();
        for (ItemType category : categories) {
            ButtonWidget button = ButtonWidget.builder(
                            translateKey(category),
                            btn -> {
                                switchCategory(category);
                                currentPage = 0;
                                prevPageButton.active = false;
                                if (!nextPageButton.active) {
                                    nextPageButton.active = true;
                                }
                            })
                    .position(buttonX, buttonY)
                    .size(buttonWidth, buttonHeight)
                    .build();

            // 初始高亮当前分类
            if (category.equals(currentCategory)) {
                button.setFocused(true);
            }

            categoryButtons.add(button);
            this.addDrawableChild(button);

            // 计算下一个按钮位置（水平排列，自动换行）
            buttonX += buttonWidth + gap;
            if (buttonX + buttonWidth > guiLeft + guiWidth - scaledWidth(MARGIN_RATIO)) {
                buttonX = scaledX(MARGIN_RATIO);
                buttonY += buttonHeight + gap;
            }
        }
    }

    /**
     * 初始化分页按钮
     */
    private void initPaginationButtons() {
        int buttonWidth = scaledWidth(0.08f);
        int buttonHeight = scaledHeight(0.060f);
        int bottomAreaY = scaledY(0.9f);

        // 上一页按钮
        prevPageButton = ButtonWidget.builder(
                        Text.literal("◀"),
                        button -> {
                            if (currentPage > 0) {
                                currentPage--;
                                nextPageButton.active = true;
                                if (currentPage == 0) {
                                    prevPageButton.active = false;
                                }
                            }
                        })
                .position(scaledX(MARGIN_RATIO), bottomAreaY)
                .size(buttonWidth, buttonHeight)
                .build();
        this.addDrawableChild(prevPageButton);
        prevPageButton.active = false;

        // 下一页按钮
        nextPageButton = ButtonWidget.builder(
                        Text.literal("▶"),
                        button -> {
                            int totalPages = (int) Math.ceil((double) currentItems.size() / ITEMS_PER_PAGE);
                            if (currentPage < totalPages - 1) {
                                currentPage++;
                                if (currentPage == totalPages - 1) nextPageButton.active = false;
                                prevPageButton.active = true;
                            }
                        })
                .position(scaledX(MARGIN_RATIO) + buttonWidth + scaledWidth(0.20f), bottomAreaY)
                .size(buttonWidth, buttonHeight)
                .build();
        this.addDrawableChild(nextPageButton);
    }

    /**
     * 初始化功能按钮
     */
    private void initFunctionButtons() {
        int buttonWidth = scaledWidth(0.16f);
        int buttonHeight = scaledHeight(0.060f);
        int bottomAreaY = scaledY(1 - BOTTOM_BUTTON_AREA_HEIGHT_RATIO);
        int margin = scaledWidth(MARGIN_RATIO);

        // 确定按钮
        doneButton = ButtonWidget.builder(
                        Text.translatable("screen.custom_item.select_item.button.done"),
                        button -> {
                            // to do
                        })
                .position(guiLeft + guiWidth - 2 * buttonWidth - margin - scaledHeight(0.03f),
                        bottomAreaY + buttonHeight + scaledHeight(0.03f))
                .size(buttonWidth, buttonHeight)
                .build();
        this.addDrawableChild(doneButton);

        // 关闭按钮
        this.addDrawableChild(ButtonWidget.builder(
                        Text.translatable("screen.custom_item.select_item.button.back"),
                        button -> this.close())
                .position(guiLeft + guiWidth - buttonWidth - margin,
                        bottomAreaY + buttonHeight + scaledHeight(0.03f))
                .size(buttonWidth, buttonHeight)
                .build());

        updateButtonState();
    }

    private void updateButtonState() {
        if (doneButton != null) {
            doneButton.active = selectedItem != null;
        }
    }

    /**
     * 切换分类（补充高亮逻辑）
     */
    private void switchCategory(ItemType category) {
        currentCategory = category;
        currentItems = itemsByCategory.get(category);

        // 高亮当前分类按钮
        for (ButtonWidget button : categoryButtons) {
            button.setFocused(false);
        }
    }

    /**
     * 过滤物品
     */
    private void filterItems(String searchText) {
        if (searchText.isEmpty()) {
            currentItems = itemsByCategory.get(currentCategory);
        } else {
            currentItems = new ArrayList<>();
            String searchLower = searchText.toLowerCase();
            for (Item item : itemsByCategory.get(currentCategory)) {
                String itemName = item.getName().getString().toLowerCase();
                if (itemName.contains(searchLower)) {
                    currentItems.add(item);
                }
            }
        }
    }

    /**
     * 检测鼠标是否点击了物品网格
     */
    private void checkItemGridClick(double mouseX, double mouseY) {
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, currentItems.size());

        // 物品网格位置计算
        int gridStartX = scaledX(MARGIN_RATIO);
        int gridStartY = scaledY(ITEM_GRID_TOP_RATIO);
        int itemSize = scaledWidth(0.045f); // 物品大小根据GUI宽度调整

        for (int i = startIndex; i < endIndex; i++) {
            int gridIndex = i - startIndex;
            int gridX = gridIndex % ITEMS_PER_ROW;
            int gridY = gridIndex / ITEMS_PER_ROW;

            // 物品网格位置（使用相对计算）
            int x = gridStartX + gridX * itemSize;
            int y = gridStartY + gridY * itemSize;

            // 检测点击范围
            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                selectedItem = currentItems.get(i);
                updateButtonState();
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 优先处理物品网格点击
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            checkItemGridClick(mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 重写renderBackground，根据标志控制是否绘制模糊背景
     */
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (shouldRenderBackground) {
            super.renderBackground(context, mouseX, mouseY, delta);
        }
    }

    /**
     * 核心：手动控制绘制顺序，避免直接访问私有字段
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. 绘制模糊背景
        shouldRenderBackground = true;
        this.renderBackground(context, mouseX, mouseY, delta);
        shouldRenderBackground = false;

        // 2. 绘制自定义GUI背景
        this.drawGuiBackground(context);

        // 3. 绘制标题
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.custom_item.title").append(translateKey(currentCategory)),
                width / 2,
                scaledY(MARGIN_RATIO),
                0xFFFFFF
        );

        // 4. 绘制物品网格
        renderItemGrid(context, mouseX, mouseY);

        // 5. 绘制分页信息
        renderPaginationInfo(context);

        // 6. 绘制选中物品信息
        renderSelectedItemInfo(context);

        // 7. 调用super.render()，此时其内部的renderBackground会被跳过，只绘制控件（最上层）
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * 绘制物品网格
     */
    private void renderItemGrid(DrawContext context, int mouseX, int mouseY) {
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, currentItems.size());

        // 动态计算物品网格位置和大小
        int gridStartX = scaledX(MARGIN_RATIO);
        int gridStartY = scaledY(ITEM_GRID_TOP_RATIO);
        int itemSize = Math.max(16, scaledWidth(0.045f)); // 最小16像素，根据GUI宽度调整

        for (int i = startIndex; i < endIndex; i++) {
            Item item = currentItems.get(i);
            ItemStack stack = new ItemStack(item);

            int gridIndex = i - startIndex;
            int gridX = gridIndex % ITEMS_PER_ROW;
            int gridY = gridIndex / ITEMS_PER_ROW;

            // 物品网格位置（使用动态计算）
            int x = gridStartX + gridX * itemSize;
            int y = gridStartY + gridY * itemSize;

            // 绘制物品
            context.drawItem(stack, x, y);
            context.drawItemInSlot(this.textRenderer, stack, x, y);

            // 鼠标悬停效果
            if (mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16) {
                // 绘制悬停高亮框
                context.fill(x - 1, y - 1, x + 17, y + 17, 0x40FFFFFF);
            }

            // 选中物品高亮（红色边框）
            if (item == selectedItem) {
                context.drawBorder(x - 1, y - 1, 18, 18, 0xFFFF0000);
            }
        }
    }

    /**
     * 绘制分页信息
     */
    private void renderPaginationInfo(DrawContext context) {
        int totalPages = Math.max(1, (int) Math.ceil((double) currentItems.size() / ITEMS_PER_PAGE));
        String pageText = String.format(" %d / %d ", currentPage + 1, totalPages);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(pageText),
                scaledX(0.20f),
                scaledY(0.918f),
                0xAAAAAA
        );

        if (totalPages == 1) {
            nextPageButton.active = false;
        }
    }

    /**
     * 绘制选中物品信息
     */
    private void renderSelectedItemInfo(DrawContext context) {
        if (selectedItem != null) {
            // 选中物品名称
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("screen.custom_item.selectedItemInfo.selected").append(selectedItem.getName()),
                    scaledX(0.10f),
                    scaledY(0.80f),
                    0x00FF00
            );

            // 选中物品大图
            int iconX = scaledX(0.05f);
            int iconY = scaledY(0.80f);
            context.drawBorder(iconX - 2, iconY - 2, 20, 20, 0xFFFFFFFF);
            context.drawItem(new ItemStack(selectedItem), iconX, iconY);
            context.drawItemInSlot(this.textRenderer, new ItemStack(selectedItem), iconX, iconY);

            // 物品id
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.translatable("screen.custom_item.selectedItemInfo.itemId").append(Text.of(Registries.ITEM.getId(selectedItem))),
                    scaledX(0.10f),
                    scaledY(0.84f),
                    0x29abb7
            );
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC关闭界面
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }

        // 数字键快速切换分类
        if (!searchField.isFocused()) {
            int categoryIndex = keyCode - GLFW.GLFW_KEY_1;
            if (categoryIndex >= 0 && categoryIndex < categories.size()) {
                switchCategory(categories.get(categoryIndex));
                currentPage = 0;
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}