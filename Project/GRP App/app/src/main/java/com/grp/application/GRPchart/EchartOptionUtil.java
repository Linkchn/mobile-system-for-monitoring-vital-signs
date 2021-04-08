package com.grp.application.GRPchart;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;

public class EchartOptionUtil {

    /**
     * This method is to create the chart x Axis and y Axis
     *
     * @param xAxis The x Axis of the chart
     * @param yAxis The y Axis of the chart
     * @param name  The name shown in the chart
     * @return the GsonOption
     * @author UNNC GRP G19
     */
    public static GsonOption getLineChartOptions(Object[] xAxis, Object[] yAxis, String name) {
        GsonOption option = new GsonOption();
        option.legend(name);
        option.tooltip().trigger(Trigger.axis);

        ValueAxis valueAxis = new ValueAxis();
        option.yAxis(valueAxis);

        CategoryAxis categorxAxis = new CategoryAxis();
        categorxAxis.axisLine().onZero(false);
        categorxAxis.boundaryGap(true);
        categorxAxis.data(xAxis);
        option.xAxis(categorxAxis);

        Line line = new Line();
        line.smooth(false).name(name).data(yAxis).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
        option.series(line);
        return option;
    }
}
