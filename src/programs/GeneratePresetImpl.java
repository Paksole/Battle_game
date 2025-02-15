package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Инициализация основных компонентов
        Army computerArmy = new Army();                    // Создаем новую армию компьютера
        Map<String, Integer> unitCounts = new HashMap<>(); // Счетчик юнитов каждого типа
        Random random = new Random();                      // Генератор случайных чисел
        int remainingPoints = maxPoints;                   // Оставшиеся очки для создания армии

        // Создание сетки возможных позиций на поле (левая сторона: x от 0 до 2)
        List<int[]> positions = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 20; y++) {
                positions.add(new int[]{x, y});
            }
        }

        // Рандомизация позиций для случайного размещения
        Collections.shuffle(positions, random);

        // Перемешивание списка доступных юнитов для разнообразия армии
        List<Unit> shuffledUnits = new ArrayList<>(unitList);
        Collections.shuffle(shuffledUnits, random);

        // Основной цикл создания армии
        for (Unit template : shuffledUnits) {
            String unitType = template.getUnitType();

            // Создаем юнитов, пока не достигнем лимитов
            while (unitCounts.getOrDefault(unitType, 0) < 11 &&    // Не более 11 юнитов одного типа
                    remainingPoints >= template.getCost() &&        // Есть достаточно очков
                    !positions.isEmpty()) {                         // Есть свободные позиции

                // Получаем следующую случайную позицию
                int[] pos = positions.remove(0);
                int count = unitCounts.getOrDefault(unitType, 0);

                // Создаем нового юнита на основе шаблона
                Unit newUnit = new Unit(
                        unitType + " " + (count + 1),      // Уникальное имя
                        template.getUnitType(),            // Тип юнита
                        template.getHealth(),              // Здоровье
                        template.getBaseAttack(),          // Базовая атака
                        template.getCost(),                // Стоимость
                        template.getAttackType(),          // Тип атаки
                        template.getAttackBonuses(),       // Бонусы атаки
                        template.getDefenceBonuses(),      // Бонусы защиты
                        pos[0],                            // Позиция X
                        pos[1]                             // Позиция Y
                );

                // Добавляем юнита в армию и обновляем счетчики
                computerArmy.getUnits().add(newUnit);
                remainingPoints -= template.getCost();
                unitCounts.put(unitType, count + 1);
            }
        }

        return computerArmy;
    }
}



