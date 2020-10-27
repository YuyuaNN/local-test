package util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class HeroCombination {
    public static Stack<Hero> stack = new Stack<Hero>();
    public static Map<String, List<Integer>> effect = new HashMap<String, List<Integer>>();
    public static Stack<Hero> stackCal = new Stack<Hero>();
    public static List<Set<Hero>> supSetList = new ArrayList<Set<Hero>>();//候选的英雄 数组
    public static Set<Hero> supSet = new HashSet<Hero>();
    private static int count = 0;//组合次数
    //查询变量
    public static String featureName = "腥红之月";//特质名称
    public static Integer heroAmount = 8;//英雄人口
    public static Integer amount = 6;//羁绊数量
    public static Integer combineAmount = 5;//羁绊组合数量

    public static void main(String[] args) {
        HeroCombination.reset();
    }

    /**
     * 重置程序
     * readme 输入 r 重置程序，输入，形如：腥红之月,8,6,5  执行程序
     */
    public static void reset() {
        //初始化 HeroCombination 变量
        HeroCombination.stack = new Stack<Hero>();
        HeroCombination.effect = new HashMap<String, List<Integer>>();
        HeroCombination.stackCal = new Stack<Hero>();
        HeroCombination.supSetList = new ArrayList<Set<Hero>>();//候选的英雄 数组
        HeroCombination.supSet = new HashSet<Hero>();
        HeroCombination.count = 0;//组合次数

        HeroCombination.featureName = "";//特质名称
        HeroCombination.heroAmount = 0;//英雄人口
        HeroCombination.amount = 0;//羁绊数量
        HeroCombination.combineAmount = 0;//羁绊组合数量

        //初始化 HeroCombination 变量
        InitHero.combineList = new ArrayList<Map>();//最终的组合结果
        System.out.println("====================Library reset!=====================");

        System.out.println("请依次输入：特质名称,英雄人口,羁绊数量,羁绊组合数量");
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) {
            scanner.close();
            System.out.println("程序正常退出！");
            return;//没有输入则 程序正常退出
        }
        String input = scanner.nextLine();
        if ("r".equals(StringUtils.trim(input)) || StringUtils.isBlank(input)) {// 输入 r 重置程序
            scanner.close();
            System.out.println("重置程序完成！");
            HeroCombination.reset();
        }
        List<String> inputList = new ArrayList<String>(Arrays.asList(StringUtils.split(input, ",")));
        if (inputList.size() != 4) {
            scanner.close();
            System.out.println("输入格式不符！请重新输入，形如：腥红之月,8,6,5");
            HeroCombination.reset();
        } else {
            HeroCombination.featureName = inputList.get(0);//特质名称
            HeroCombination.heroAmount = Integer.valueOf(inputList.get(1));//英雄人口
            HeroCombination.amount = Integer.valueOf(inputList.get(2));//羁绊数量
            HeroCombination.combineAmount = Integer.valueOf(inputList.get(3));//羁绊组合数量
            //初始化开始
            HeroCombination.init();
            scanner.close();
            System.out.println("====================End of execution!=====================");
        }
    }

    /**
     * 执行程序入口
     */
    public static void init() {
        HeroCombination.effect = InitEffect.initEffect();//初始化羁绊特效
//        Hero[] heroes = InitHero.initHero();//初始化英雄池
        Map<String, Set<Hero>> heroes = InitHero.initHero(featureName);//初始化英雄池
        combineCal(heroes.get("sup").toArray(new Hero[0]), (heroAmount - amount) > 0 ? (heroAmount - amount) : 0, 0, 0); // 选出候选英雄
        Set<Hero> heroSet = null;
        for (Set<Hero> set : supSetList) {
            heroSet = new HashSet<Hero>() {{
                addAll(heroes.get("main"));
            }};
            heroSet.addAll(set);
            Hero[] heroeArray = heroSet.toArray(new Hero[0]);
            combine(heroeArray, heroAmount, 0, 0); // 从英雄池数组中选择N个英雄
        }
//        List<Map> combineList = InitHero.combineList.stream().sorted(Comparator.comparingInt(s -> (Integer) s.get("combineCount"))).collect(Collectors.toList());
        List<Map> combineList = InitHero.combineList.stream().sorted(Comparator.comparingInt(HeroCombination::comparingByCombineCount)
                .thenComparing(HeroCombination::comparingByFee).reversed()).collect(Collectors.toList());//先根据 combineAmount 降序排序，再根据 fee 降序排序
//        List<Map> combineList = InitHero.combineList.stream().sorted(((s1, s2) -> {
//                    Integer combineCount = (Integer) s1.get("combineCount");
//                    Integer combineCount2 = (Integer) s2.get("combineCount");
//                    Integer fee = (Integer) s1.get("fee");
//                    Integer fee2 = (Integer) s2.get("fee");
//                    if (combineCount > combineCount2) {
//                        return -1;//倒序
//                    } else if (combineCount < combineCount2) {
//                        return 1;
//                    } else if (combineCount == combineCount2) {
//                        if (fee > fee2) {
//                            return -1;//倒序
//                        } else if (fee < fee2) {
//                            return 1;
//                        } else if (fee == fee) {
//                            return 0;
//                        }
//                    }
//                    return 0;
//                })
//        ).collect(Collectors.toList());
        //打印结果
        combineList.forEach(resultMap -> {
            System.out.println(resultMap.get("heroList"));
            System.out.println(resultMap.get("combineMap"));
            System.out.println("羁绊数量：" + resultMap.get("combineCount"));
            System.out.println("总价：" + resultMap.get("fee"));
        });
        System.out.println("组合次数：" + HeroCombination.count);
    }


    /**
     * @param array        元素
     * @param toPickNum    要选多少个元素
     * @param selectedNum  当前有多少个元素
     * @param currentIndex 当前选到的下标
     */
    private static void combine(Hero[] array, int toPickNum, int selectedNum, int currentIndex) {
        if (selectedNum == toPickNum) {
//            System.out.println(stack);
            Map<String, Integer> map = new HashMap<String, Integer>();
            stack.forEach(hero -> {
                hero.getFeatures().forEach(feature -> {
                    if (!map.containsKey(feature)) {
                        map.put(feature, 1);
                    } else {
                        map.put(feature, map.get(feature) + 1);
                    }
                });
            });
//            System.out.println(map);
            //按条件过滤 英雄组合
//            Map<String, Integer> filterMap = InitHero.filterMapCondition(map, null, null);
            Map<String, Integer> filterMap = InitHero.filterMapCondition(map, featureName, amount, combineAmount);

            filterMap.forEach((k, v) -> {
                InitHero.printHeroFetters(stack);
                HeroCombination.count++;
            });
//            HeroCombination.count++;
            return;
        }

        for (int i = currentIndex; i < array.length; i++) {
            if (!stack.contains(array[i])) {
                stack.add(array[i]);
                combine(array, toPickNum, selectedNum + 1, i);
                stack.pop();
            }
        }
    }

    /**
     * @param array        元素
     * @param toPickNum    要选多少个元素
     * @param selectedNum  当前有多少个元素
     * @param currentIndex 当前选到的下标
     */
    private static void combineCal(Hero[] array, int toPickNum, int selectedNum, int currentIndex) {
        if (selectedNum == toPickNum) {
            supSet = new HashSet<Hero>();
            stackCal.forEach(e -> {
                supSet.add(e);
            });
            supSetList.add(supSet);
            return;
        }

        for (int i = currentIndex; i < array.length; i++) {
            if (!stackCal.contains(array[i])) {
                stackCal.add(array[i]);
                combineCal(array, toPickNum, selectedNum + 1, i);
                stackCal.pop();
            }
        }
    }

    public static Integer comparingByCombineCount(Map map) {
        return (Integer) map.get("combineCount");
    }

    public static Integer comparingByFee(Map map) {
        return (Integer) map.get("fee");
    }

}
