package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Настройка параметров поиска в зависимости от целевой армии
        int startRow = isLeftArmyTarget ? 0 : 2;          // Начальный ряд (0 для левой армии, 2 для правой)
        int direction = isLeftArmyTarget ? 1 : -1;        // Направление проверки (1 вправо, -1 влево)

        // Создание карты занятых позиций для каждого ряда
        Map<Integer, Set<Integer>> occupiedPositions = new HashMap<>();
        for (int row = 0; row < unitsByRow.size(); row++) {
            occupiedPositions.put(row, new HashSet<>());
            // Заполнение карты позициями живых юнитов
            for (Unit unit : unitsByRow.get(row)) {
                if (unit.isAlive()) {
                    occupiedPositions.get(row).add(unit.getyCoordinate());
                }
            }
        }

        // Проверка каждого ряда на наличие подходящих для атаки юнитов
        for (int row = 0; row < unitsByRow.size(); row++) {
            for (Unit unit : unitsByRow.get(row)) {
                // Пропускаем мертвых юнитов
                if (!unit.isAlive()) {
                    continue;
                }

                // Юниты в первом ряду всегда могут атаковать
                if (row == startRow) {
                    suitableUnits.add(unit);
                    continue;
                }

                // Проверка, не блокируется ли юнит другими юнитами
                boolean isBlocked = false;
                int checkRow = row - direction;
                int unitY = unit.getyCoordinate();

                // Проверка всех рядов между юнитом и целью
                while (checkRow >= 0 && checkRow < unitsByRow.size()) {
                    if (occupiedPositions.get(checkRow).contains(unitY)) {
                        isBlocked = true;
                        break;
                    }
                    checkRow -= direction;
                }

                // Если юнит не заблокирован, добавляем его в список подходящих
                if (!isBlocked) {
                    suitableUnits.add(unit);
                }
            }
        }

        return suitableUnits;
    }
}

