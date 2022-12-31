package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Максим Кожуховский
 * mail: m.kozhukhovskii@g.nsu.ru
 * */

public class Main {

  public static void main(String[] args) throws IOException {
    Scanner in = new Scanner(Path.of("tests.txt"), StandardCharsets.UTF_8);
    PrintWriter out = new PrintWriter("out.txt", StandardCharsets.UTF_8);

    int n;
    int m;
    int k;
    int x;
    int y;
    int numCase = 1;
    int[] outPizza;

    while (in.hasNextInt()) {
      n = in.nextInt();
      if (n == 0) break;
      m = in.nextInt();
      k = in.nextInt();

      int[][] city = new int[n][m];
      Pizzeria[] pizzerias = new Pizzeria[k];

      for (var c : city) Arrays.fill(c, -1);
      for (int i = 0; i < k; ++i) {
        x = in.nextInt() - 1;
        y = in.nextInt() - 1;
        pizzerias[i] = new Pizzeria(x, y, i, in.nextInt());
        city[x][y] = i;
      }

      fill(city, pizzerias);

      out.write("case " + numCase + ":\n");
      for (int i = 0; i < pizzerias.length; ++i) {
        outPizza = routePizzeria(city, pizzerias[i]);
        for (int dir = 0; dir < 4; ++dir) out.write(outPizza[dir] + " ");
        out.write("\n");
      }
      out.write("\n");
      ++numCase;
    }
    out.flush();
  }

  /**
   * Метод, который определяет колиство свободных блоков,
   * от пицеррии до указанного блока
   * @param city город
   * @param x координата блока, до которого нужно измерить
   * @param y координата блока
   * @param pizza пиццерия */
  public static int freeBlocks(int[][] city, int x, int y, Pizzeria pizza) {
    int counter = 0;
    if (x == pizza.x) {
      if (y > pizza.y) {
        for (int i = pizza.y + 1; i <= y; ++i) {
          if (city[x][i] == -1) {
            counter += 1;
          } else if (city[x][i] != pizza.ind) return 0;
        }
      } else {
        for (int i = pizza.y - 1; i >= y; --i) {
          if (city[x][i] == -1) {
            counter += 1;
          } else if (city[x][i] != pizza.ind) return 0;
        }
      }
    } else {
      if (x > pizza.x) {
        for (int i = pizza.x + 1; i <= x; ++i) {
          if (city[i][y] == -1) {
            counter += 1;
          } else if (city[i][y] != pizza.ind) return 0;
        }
      } else {
        for (int i = pizza.x - 1; i >= x; --i) {
          if (city[i][y] == -1) {
            counter += 1;
          } else if (city[i][y] != pizza.ind) return 0;
        }
      }
    }
    return counter;
  }
  /**
   * Метод, который проверяет, можно ли заполнить блок (x, y)
   * пиццерией
   * @param x
   * @param y
   * @param pizza пиццерия, которой заполняется блок
   * @param gap количество свободных блоков между пиццерией и (x, y)
   * @return true ,если возможно заполнить, false инвче*/
  public static boolean isFills(int x, int y, Pizzeria pizza, int gap) {
    if (x != pizza.x && y != pizza.y || gap == 0) return false;
    return (x == pizza.x && gap <= pizza.getC()) || (y == pizza.y && gap <= pizza.getC());
  }

  /**
   * Метод, который возвращет список помещаемых(в данный блок) пиццерий
   * @param city город
   * @param x Координаты блока, в который помещаются пиццерии
   * @param y
   * @param pizzerias массив пиццерий
   * @return список целых номеров пиццерий*/
  public static ArrayList<Pizzeria> posPizzerias(int[][] city, int x, int y, Pizzeria[] pizzerias) {
    ArrayList<Pizzeria> pos_pizzerias = new ArrayList<>();
    for(var pizza : pizzerias) {
      if (isFills(x, y, pizza, freeBlocks(city, x, y, pizza))) {
        pos_pizzerias.add(pizza);
      }
    }
    return pos_pizzerias;
  }

  /**
   * Метод, который заполняет все блоки от пиццерии до блока (x, y)
   * @param city
   * @param x
   * @param y
   * @param pizza
   * @return количество заполненых блоков*/
  public static int fillBlock(int[][] city, int x, int y, Pizzeria pizza) {
    int counter = 0;
    if (x == pizza.x) {
      if (y > pizza.y) {
        for (int i = pizza.y + 1; i <= y; ++i)
          if (city[x][i] == -1) {
            city[x][i] = pizza.ind;
            counter += 1;
          }
      } else {
        for (int i = pizza.y - 1; i >= y; --i)
          if (city[x][i] == -1) {
            city[x][i] = pizza.ind;
            counter += 1;
          }
      }
    } else {
      if (x > pizza.x) {
        for (int i = pizza.x + 1; i <= x; ++i)
          if (city[i][y] == -1) {
            city[i][y] = pizza.ind;
            counter += 1;
          }
      } else {
        for (int i = pizza.x - 1; i >= x; --i)
          if (city[i][y] == -1) {
            city[i][y] = pizza.ind;
            counter += 1;
          }
      }
    }
    return counter;
  }


  /**
   * Метод, который находит возможные блоки, для заполнения пиццерией
   * @param city
   * @param pizza
   * @return список двойных массивов, которые являются координатами блоков*/
  public static ArrayList<int[]> possibleBlocks(int[][] city, Pizzeria pizza) {
    ArrayList<int[]> result = new ArrayList<>();
    int gaps;
    for (int x = 0; x < city.length; ++x) {
      for (int y = 0; y < city[0].length; ++y) {
        if (city[x][y] == -1) {
          gaps = freeBlocks(city, x, y, pizza);
          if (isFills(x, y, pizza, gaps) && gaps > 0) result.add(new int[] {x, y, gaps});
        }
      }
    }
    return result;
  }

  /**
   * Метод, который определяет, можно ли заполнить блок (x, y)
   * какой-то пиццерией из списка, так что бы для других пиццерий остались
   * возможные блоки для заполнеия
   * @param city
   * @param x
   * @param y
   * @param pos_pizzerias возможные пиццерии
   * @return true, если этот блок можно заполнить какой-то пиццерией из списка,
   * false - иначе*/
  public static boolean numPizzaToFill(
      int[][] city, int x, int y, ArrayList<Pizzeria> pos_pizzerias) {
    ArrayList<int[]> pos_blocks;

    for (var p1 : pos_pizzerias) {
      city[x][y] = p1.ind;
      for (var p2 : pos_pizzerias) {
        if (p2.equals(p1)) continue;
        pos_blocks = possibleBlocks(city, p2);
        if (pos_blocks.size() >= p2.getC()) {
          city[x][y] = -1;
          p1.downC(fillBlock(city, x, y, p1));
          return true;
        }
      }
      city[x][y] = -1;
    }
    return false;
  }

  /**
   * Метод, реализующий "простое" заполнение, то есть заполнение всех
   * единственно взоможных(всмысле пиццерий) блоков
   * @param city город
   * @param pizzerias пиццерии
   * @return true - если какой-то блок был заполнен, false - иначе*/
  public static boolean simpleFill(int[][] city, Pizzeria[] pizzerias) {
    boolean flag = false;
    ArrayList<Pizzeria> pos_pizzerias;
    Pizzeria currPizza;
    for (int x = 0; x < city.length; ++x) {
      for (int y = 0; y < city[0].length; ++y) {
        if (city[x][y] == -1) {
          pos_pizzerias = posPizzerias(city, x, y, pizzerias);
          if (pos_pizzerias.size() == 1) {
            currPizza = pos_pizzerias.get(0);
            currPizza.downC(fillBlock(city, x, y, currPizza));
            flag = true;
          }
        }
      }
    }
    return flag;
  }

  /**
   * Метод, реализующий "сложное" заполнение, то есть заполнение блоков,
   * у которых несколько кандидатов для заполнения
   * @param city
   * @param pizzerias
   * @return true - если хотя-бы один блок был заполнен, false иначе*/
  public static boolean hardFill(int[][] city, Pizzeria[] pizzerias) {
    ArrayList<Pizzeria> pos_pizzerias;
    for (int x = 0; x < city.length; ++x) {
      for (int y = 0; y < city[0].length; ++y) {
        if (city[x][y] == -1) {
          pos_pizzerias = posPizzerias(city, x, y, pizzerias);
          if (numPizzaToFill(city, x, y, pos_pizzerias)) return true;
        }
      }
    }
    return false;
  }

  /**
   * метод заполнения, заполняет всегда блоки, с единстенным вариантом пиццерий,
   * затем, только блоки с несколькими вариантами
   * @param city
   * @param pizzerias*/
  public static void fill(int[][] city, Pizzeria[] pizzerias) {
    boolean currentFlag = true;
    while (currentFlag) {
      currentFlag = simpleFill(city, pizzerias);
      if (!currentFlag) currentFlag = hardFill(city, pizzerias);
    }
  }

  /**
   * метод, который для пиццерии определяет, количество потраченных блоков
   * в каждом направлении
   * @param city
   * @param pizza
   * @return массив целлых чисел, колличество пиццерий в направлении n, e, s, w*/
  public static int[] routePizzeria(int[][] city, Pizzeria pizza) {
    int[] result = new int[4];
    Arrays.fill(result, 0);
    for (int y = pizza.y + 1; y < city[0].length; ++y) {
      if (city[pizza.x][y] == pizza.ind) result[0] += 1;
      else break;
    }
    for (int x = pizza.x + 1; x < city.length; ++x) {
      if (city[x][pizza.y] == pizza.ind) result[1] += 1;
      else break;
    }
    for (int y = pizza.y - 1; y >= 0; --y) {
      if (city[pizza.x][y] == pizza.ind) result[2] += 1;
      else break;
    }
    for (int x = pizza.x - 1; x >= 0; --x) {
      if (city[x][pizza.y] == pizza.ind) result[3] += 1;
      else break;
    }
    return result;
  }
}
