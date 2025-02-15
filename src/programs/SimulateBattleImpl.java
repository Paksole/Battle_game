package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    // Интерфейс для вывода логов сражения
    private final PrintBattleLog printBattleLog;

    // Конструктор с базовой реализацией логгера
    public SimulateBattleImpl() {
        this.printBattleLog = new PrintBattleLog() {
            @Override
            public void printBattleLog(Unit attackingUnit, Unit target) {
                System.out.println(attackingUnit.getName() + " атакует " + target.getName());
            }
        };
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        int round = 1;

        // Основной цикл сражения
        while (true) {
            System.out.println("\nРаунд " + round);

            // Формирование списка живых юнитов обеих армий
            List<Unit> aliveUnits = new ArrayList<>();
            // Добавление живых юнитов игрока
            for (Unit unit : playerArmy.getUnits()) {
                if (unit != null && unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }
            // Добавление живых юнитов компьютера
            for (Unit unit : computerArmy.getUnits()) {
                if (unit != null && unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }

            // Подсчет живых юнитов в каждой армии
            long playerAliveCount = playerArmy.getUnits().stream()
                    .filter(u -> u != null && u.isAlive())
                    .count();
            long computerAliveCount = computerArmy.getUnits().stream()
                    .filter(u -> u != null && u.isAlive())
                    .count();

            // Вывод текущего состояния армий
            System.out.println("Армия игрока: " + playerAliveCount + " живых юнитов");
            System.out.println("Армия компьютера: " + computerAliveCount + " живых юнитов");

            // Проверка условий окончания битвы
            if (playerAliveCount == 0 || computerAliveCount == 0) {
                String winner = playerAliveCount > computerAliveCount ? "Игрок" : "Компьютер";
                System.out.println("Битва окончена! Победитель: " + winner);
                break;
            }

            // Сортировка юнитов по силе атаки (сильнейшие ходят первыми)
            aliveUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));

            // Выполнение ходов каждым юнитом
            for (Unit unit : aliveUnits) {
                Unit target = unit.getProgram().attack();
                if (target != null) {
                    printBattleLog.printBattleLog(unit, target);
                    Thread.sleep(1000); // Пауза между атаками
                }
            }

            round++;
        }
    }

    // Проверка наличия живых юнитов в обеих армиях
    private boolean hasUnitsFromBothArmies(List<Unit> units, Army playerArmy, Army computerArmy) {
        boolean hasPlayerUnits = units.stream().anyMatch(u -> playerArmy.getUnits().contains(u));
        boolean hasComputerUnits = units.stream().anyMatch(u -> computerArmy.getUnits().contains(u));
        return hasPlayerUnits && hasComputerUnits;
    }

    // Объявление победителя
    private void announceWinner(Army playerArmy, Army computerArmy) {
        long playerAlive = playerArmy.getUnits().stream().filter(Unit::isAlive).count();
        long computerAlive = computerArmy.getUnits().stream().filter(Unit::isAlive).count();
        String winner = playerAlive > computerAlive ? "Игрок" : "Компьютер";
        System.out.println("Битва окончена! Победитель: " + winner);
    }

    // Вывод текущего статуса армий
    private void printArmyStatus(Army playerArmy, Army computerArmy) {
        long playerAlive = playerArmy.getUnits().stream().filter(Unit::isAlive).count();
        long computerAlive = computerArmy.getUnits().stream().filter(Unit::isAlive).count();
        System.out.println("Армия игрока: " + playerAlive + " живых юнитов");
        System.out.println("Армия компьютера: " + computerAlive + " живых юнитов");
    }
}

