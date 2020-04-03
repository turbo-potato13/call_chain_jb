package com.company.callchain;

import com.company.callchain.exception.SyntaxErrorException;
import com.company.callchain.exception.TypeErrorException;
import com.company.callchain.function.FunctionExecutor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class FunctionExecutorTest {

    @Test
    public void testExecuteReturnsCombinedFilters() {
        final var executor = new FunctionExecutor("filter{(element>10)}%>%filter{(element<20)}");
        final var result = executor.execute();
        Assert.assertThat(result, CoreMatchers.startsWith("filter{(element>10)&(element<20)}"));
        Assert.assertEquals(0, result.lastIndexOf("filter"));
    }

    @Test(expected = SyntaxErrorException.class)
    public void testExecuteThrowsSyntaxError() {
        new FunctionExecutor("filter{(element>0)}%>%filter{(element<0)}%>%map{(element*element)").execute();
    }

    @Test(expected = TypeErrorException.class)
    public void testExecuteThrowsTypeError(){
        new FunctionExecutor("map{(element+10)}filter{(element>10)}%>%map{(element*element)}").execute();
    }

    @Test
    public void testExecuteAddsOneMap() {
        final var executor = new FunctionExecutor("map{(element-100)}");
        final var result = executor.execute();
        Assert.assertThat(result, CoreMatchers.startsWith("map{element-100}%>%map{element}"));
    }

    @Test
    public void testExecuteAddsOnlyMap() {
        final var executor = new FunctionExecutor("map{(element+20)}%>%map{(element-40)}%>%map{(element*element)}");
        final var result = executor.execute();
        Assert.assertThat(result, CoreMatchers.startsWith("map{(element+20)-40}%>%map{(element+20)*(element+20)}"));
    }

    @Test
    public void testExecuteAddMapToSimpleChain() {
        final var executor = new FunctionExecutor("filter{(element>10)}%>%map{(element+20)}");
        final var result = executor.execute();
        Assert.assertThat(result, CoreMatchers.startsWith("filter{(element>10)}%>%map{element+20}%>%map{element}"));
    }

}
