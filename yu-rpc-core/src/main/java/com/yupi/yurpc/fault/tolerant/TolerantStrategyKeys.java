package com.yupi.yurpc.fault.tolerant;

/**
 * 容错策略键名常量
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">鱼皮的编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航学习圈</a>
 */
public interface TolerantStrategyKeys {

    String FAIL_BACK = "failBack";

    String FAIL_FAST = "failFast";

    String FAIL_OVER = "failOver";

    String FAIL_SAFE = "failSafe";

}
