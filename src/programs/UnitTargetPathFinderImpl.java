package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    // Константы размеров игрового поля
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    // Возможные направления движения (включая диагональные)
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},      // Основные направления
            {-1, -1}, {1, 1}, {-1, 1}, {1, -1}     // Диагональные направления
    };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Создание множества препятствий на поле
        Set<String> obstacles = createObstaclesSet(existingUnitList, attackUnit, targetUnit);

        // Структуры данных для алгоритма A*
        PriorityQueue<Node> openSet = new PriorityQueue<>();      // Очередь для обработки узлов
        Map<String, Node> allNodes = new HashMap<>();             // Все созданные узлы
        Set<String> closedSet = new HashSet<>();                  // Обработанные узлы

        // Инициализация начального узла
        Node startNode = new Node(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        startNode.g = 0;                                          // Стоимость пути от старта
        startNode.h = calculateHeuristic(startNode.x, startNode.y, targetUnit);  // Эвристическая оценка
        startNode.f = startNode.g + startNode.h;                  // Общая стоимость пути

        openSet.add(startNode);
        allNodes.put(nodeKey(startNode.x, startNode.y), startNode);

        // Основной цикл алгоритма A*
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Проверка достижения цели
            if (current.x == targetUnit.getxCoordinate() && current.y == targetUnit.getyCoordinate()) {
                return reconstructPath(current);
            }

            closedSet.add(nodeKey(current.x, current.y));

            // Проверка всех возможных направлений движения
            for (int[] dir : DIRECTIONS) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                String newKey = nodeKey(newX, newY);

                // Пропуск недопустимых позиций
                if (!isValidPosition(newX, newY, obstacles, targetUnit) ||
                        closedSet.contains(newKey)) {
                    continue;
                }

                // Расчет стоимости пути до соседнего узла
                int tentativeG = current.g + 1;
                Node neighbor = allNodes.computeIfAbsent(newKey, k -> new Node(newX, newY));

                // Обновление пути, если найден более короткий
                if (!openSet.contains(neighbor) || tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.h = calculateHeuristic(newX, newY, targetUnit);
                    neighbor.f = neighbor.g + neighbor.h;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();  // Возврат пустого пути, если путь не найден
    }

    // Вспомогательные методы
    private Set<String> createObstaclesSet(List<Unit> units, Unit attacker, Unit target) {
        Set<String> obstacles = new HashSet<>();
        for (Unit unit : units) {
            if (unit != attacker && unit != target && unit.isAlive()) {
                obstacles.add(nodeKey(unit.getxCoordinate(), unit.getyCoordinate()));
            }
        }
        return obstacles;
    }

    // Проверка валидности позиции
    private boolean isValidPosition(int x, int y, Set<String> obstacles, Unit target) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT &&
                (!obstacles.contains(nodeKey(x, y)) ||
                        (x == target.getxCoordinate() && y == target.getyCoordinate()));
    }

    // Расчет эвристической оценки расстояния
    private int calculateHeuristic(int x, int y, Unit target) {
        return Math.max(
                Math.abs(x - target.getxCoordinate()),
                Math.abs(y - target.getyCoordinate())
        );
    }

    // Создание уникального ключа для позиции
    private String nodeKey(int x, int y) {
        return x + "," + y;
    }

    // Восстановление пути от цели к началу
    private List<Edge> reconstructPath(Node endNode) {
        List<Edge> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(new Edge(current.x, current.y));
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    // Внутренний класс для представления узла в алгоритме A*
    private static class Node implements Comparable<Node> {
        int x, y;           // Координаты
        int f, g, h;        // f = g + h, где g - стоимость пути, h - эвристическая оценка
        Node parent;        // Родительский узел для восстановления пути

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }
}

