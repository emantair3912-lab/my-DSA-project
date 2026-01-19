import java.util.*;

public class MazeGrid {
    private int[][] maze;
    private int ballX, ballY;
    private int endX, endY;
    private int steps = 0;
    private Difficulty difficulty;

    public MazeGrid(Difficulty difficulty) {
        System.out.println("MazeGrid constructor called with difficulty: " + difficulty);
        this.difficulty = difficulty;

        int size = getDifficultySize(difficulty);
        System.out.println("Maze size: " + size + "x" + size);

        maze = new int[size][size];

        // Keep generating until we have multiple paths
        boolean hasMultiplePaths = false;
        int attempts = 0;
        while (!hasMultiplePaths && attempts < 10) {
            attempts++;
            generateMaze(size);

            // Set positions before checking paths
            ballX = 1;
            ballY = 1;
            endX = size - 2;
            endY = size - 2;

            hasMultiplePaths = verifyMultiplePaths();
            if (!hasMultiplePaths) {
                System.out.println("Only one path found, regenerating... (attempt " + attempts + ")");
            }
        }

        maze[ballY][ballX] = 2; // Mark start
        maze[endY][endX] = 3;   // Mark end

        System.out.println("MazeGrid initialized successfully");
        System.out.println("Start: (" + ballX + ", " + ballY + ")");
        System.out.println("End: (" + endX + ", " + endY + ")");

        printMazeStats();
    }

    private int getDifficultySize(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return 15;    // Made slightly bigger
            case MEDIUM: return 21;
            case HARD: return 31;
            case EXPERT: return 41;
            default: return 15;
        }
    }

    private void generateMaze(int size) {
        // Initialize all as walls
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                maze[y][x] = 1;
            }
        }

        // Create base maze with recursive backtracking
        Stack<int[]> stack = new Stack<>();
        Random rand = new Random();

        maze[1][1] = 0;
        stack.push(new int[]{1, 1});

        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            List<int[]> neighbors = new ArrayList<>();
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX > 0 && newX < size - 1 && newY > 0 && newY < size - 1 && maze[newY][newX] == 1) {
                    neighbors.add(new int[]{newX, newY, dir[0], dir[1]});
                }
            }

            if (!neighbors.isEmpty()) {
                int[] next = neighbors.get(rand.nextInt(neighbors.size()));
                int newX = next[0];
                int newY = next[1];
                int dirX = next[2];
                int dirY = next[3];

                maze[y + dirY / 2][x + dirX / 2] = 0;
                maze[newY][newX] = 0;

                stack.push(new int[]{newX, newY});
            } else {
                stack.pop();
            }
        }

        // AGGRESSIVELY create multiple paths
        createMultiplePaths(size);
    }

    private void createMultiplePaths(int size) {
        Random rand = new Random();

        // MUCH more aggressive path creation
        int extraPaths;
        switch (difficulty) {
            case EASY:
                extraPaths = (size * size) / 3;  // 33% of cells
                break;
            case MEDIUM:
                extraPaths = (size * size) / 4;  // 25% of cells
                break;
            case HARD:
                extraPaths = (size * size) / 6;  // 16% of cells
                break;
            case EXPERT:
                extraPaths = (size * size) / 8;  // 12% of cells
                break;
            default:
                extraPaths = (size * size) / 4;
        }

        System.out.println("Attempting to create " + extraPaths + " alternative paths...");

        int pathsCreated = 0;
        int attempts = 0;
        int maxAttempts = size * size * 2;

        while (pathsCreated < extraPaths && attempts < maxAttempts) {
            attempts++;

            int x = rand.nextInt(size - 2) + 1;
            int y = rand.nextInt(size - 2) + 1;

            if (maze[y][x] == 0) continue;

            // Count path neighbors
            int pathNeighbors = 0;
            if (y > 0 && maze[y-1][x] == 0) pathNeighbors++;
            if (y < size - 1 && maze[y+1][x] == 0) pathNeighbors++;
            if (x > 0 && maze[y][x-1] == 0) pathNeighbors++;
            if (x < size - 1 && maze[y][x+1] == 0) pathNeighbors++;

            // Remove wall if it connects paths
            if (pathNeighbors >= 2) {
                maze[y][x] = 0;
                pathsCreated++;
            }
        }

        System.out.println("Created " + pathsCreated + " alternative paths");

        // ADDITIONAL: Create some deliberate alternative routes
        createDeliberateAlternatives(size);
    }

    private void createDeliberateAlternatives(int size) {
        Random rand = new Random();

        // Create 3-5 deliberate alternative corridors
        int corridors = 3 + rand.nextInt(3);

        for (int c = 0; c < corridors; c++) {
            // Pick random starting point in a path
            for (int attempt = 0; attempt < 100; attempt++) {
                int startX = rand.nextInt(size - 4) + 2;
                int startY = rand.nextInt(size - 4) + 2;

                if (maze[startY][startX] != 0) continue;

                // Create a short corridor in a random direction
                int direction = rand.nextInt(4);
                int length = 3 + rand.nextInt(5);

                int dx = 0, dy = 0;
                if (direction == 0) dy = -1;      // up
                else if (direction == 1) dy = 1;  // down
                else if (direction == 2) dx = -1; // left
                else dx = 1;                       // right

                int x = startX;
                int y = startY;

                for (int i = 0; i < length; i++) {
                    x += dx;
                    y += dy;

                    if (x <= 0 || x >= size - 1 || y <= 0 || y >= size - 1) break;

                    maze[y][x] = 0;

                    // Occasionally branch
                    if (rand.nextDouble() < 0.3) {
                        if (dx != 0 && y > 1 && y < size - 2) {
                            maze[y + (rand.nextBoolean() ? 1 : -1)][x] = 0;
                        } else if (dy != 0 && x > 1 && x < size - 2) {
                            maze[y][x + (rand.nextBoolean() ? 1 : -1)] = 0;
                        }
                    }
                }
                break;
            }
        }

        System.out.println("Created " + corridors + " deliberate alternative corridors");
    }

    private boolean verifyMultiplePaths() {
        // Count distinct paths using modified BFS
        int pathCount = countDistinctPaths();
        System.out.println("Found " + pathCount + " distinct path(s) to goal");
        return pathCount > 1;
    }

    private int countDistinctPaths() {
        // Use DFS to find multiple distinct paths
        List<List<int[]>> allPaths = new ArrayList<>();
        List<int[]> currentPath = new ArrayList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];

        currentPath.add(new int[]{ballX, ballY});
        visited[ballY][ballX] = true;

        findPathsDFS(ballX, ballY, visited, currentPath, allPaths, 0);

        return allPaths.size();
    }

    private void findPathsDFS(int x, int y, boolean[][] visited,
                              List<int[]> currentPath, List<List<int[]>> allPaths, int depth) {
        // Limit depth to avoid infinite recursion
        if (depth > 1000 || allPaths.size() >= 10) return;

        if (x == endX && y == endY) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX >= 0 && newX < maze[0].length &&
                    newY >= 0 && newY < maze.length &&
                    !visited[newY][newX] && maze[newY][newX] != 1) {

                visited[newY][newX] = true;
                currentPath.add(new int[]{newX, newY});

                findPathsDFS(newX, newY, visited, currentPath, allPaths, depth + 1);

                visited[newY][newX] = false;
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    public int[][] getMaze() {
        return maze;
    }

    public int getBallX() {
        return ballX;
    }

    public int getBallY() {
        return ballY;
    }

    public int getSteps() {
        return steps;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setBallPosition(int x, int y) {
        if (x >= 0 && x < maze[0].length && y >= 0 && y < maze.length && maze[y][x] != 1) {
            ballX = x;
            ballY = y;
            steps++;
        }
    }

    public boolean moveUp() {
        if (ballY > 0 && maze[ballY - 1][ballX] != 1) {
            ballY--;
            steps++;
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if (ballY < maze.length - 1 && maze[ballY + 1][ballX] != 1) {
            ballY++;
            steps++;
            return true;
        }
        return false;
    }

    public boolean moveLeft() {
        if (ballX > 0 && maze[ballY][ballX - 1] != 1) {
            ballX--;
            steps++;
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        if (ballX < maze[0].length - 1 && maze[ballY][ballX + 1] != 1) {
            ballX++;
            steps++;
            return true;
        }
        return false;
    }

    public boolean isFinished() {
        return ballX == endX && ballY == endY;
    }

    public List<int[]> getShortestPath() {
        List<int[]> path = new ArrayList<>();

        Queue<Node> queue = new LinkedList<>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        Node[][] parent = new Node[maze.length][maze[0].length];

        queue.add(new Node(ballX, ballY));
        visited[ballY][ballX] = true;

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.x == endX && current.y == endY) {
                Node node = current;
                while (node != null) {
                    path.add(0, new int[]{node.x, node.y});
                    node = parent[node.y][node.x];
                }
                System.out.println("Shortest path length: " + path.size());
                return path;
            }

            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (newX >= 0 && newX < maze[0].length &&
                        newY >= 0 && newY < maze.length &&
                        !visited[newY][newX] && maze[newY][newX] != 1) {

                    visited[newY][newX] = true;
                    parent[newY][newX] = current;
                    queue.add(new Node(newX, newY));
                }
            }
        }

        System.out.println("No path found!");
        return path;
    }

    public void printMazeStats() {
        int totalCells = maze.length * maze[0].length;
        int wallCells = 0;
        int pathCells = 0;

        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                if (maze[y][x] == 1) wallCells++;
                else pathCells++;
            }
        }

        System.out.println("\n=== MAZE STATISTICS ===");
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Size: " + maze[0].length + "x" + maze.length);
        System.out.println("Path cells: " + pathCells + " (" + (pathCells * 100 / totalCells) + "%)");
        System.out.println("Wall cells: " + wallCells + " (" + (wallCells * 100 / totalCells) + "%)");

        List<int[]> shortestPath = getShortestPath();
        System.out.println("Shortest path: " + shortestPath.size() + " steps");

        // Reset ball position after pathfinding
        ballX = 1;
        ballY = 1;

        System.out.println("======================\n");
    }

    // ASCII visualization for debugging
    public void printMaze() {
        System.out.println("\n=== MAZE VISUALIZATION ===");
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[0].length; x++) {
                if (x == ballX && y == ballY) System.out.print("@ ");
                else if (x == endX && y == endY) System.out.print("E ");
                else if (maze[y][x] == 1) System.out.print("█ ");
                else System.out.print("  ");
            }
            System.out.println();
        }
        System.out.println("========================\n");
    }

    private static class Node {
        int x, y;
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
