package com.sheldon.mybatis.flex.spring.enhance.pageable;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;

public class SpringPageableQueryChain<T>  extends QueryWrapperAdapter<SpringPageableQueryChain<T>> implements MapperQueryChain<T> {
    private final static Field[] BASE_FIELDS = BaseQueryWrapper.class.getDeclaredFields();
    private final QueryChain<T> queryChain;

    private SpringPageableQueryChain(QueryChain<T> queryChain) {
        this.queryChain = queryChain;
        try {
            for (Field field : BASE_FIELDS) {
                field.setAccessible(true);
                Object value = field.get(queryChain);
                field.set(this, value);
            }
        } catch (IllegalAccessException ignored) {
        }
    }

    public static <T> SpringPageableQueryChain<T> of(QueryChain<T> queryChain) {
        return new SpringPageableQueryChain<>(queryChain);
    }

    public Page<T> page(Pageable pageable) {
        return this.baseMapper().paginate(pageable2Page(pageable), this.toQueryWrapper());
    }

    public <R> Page<R> pageAs(Pageable pageable, Class<R> asType) {
        return this.baseMapper().paginateAs(pageable2Page(pageable), this.toQueryWrapper(), asType);
    }

    public org.springframework.data.domain.Page<T> pageSpring(Pageable pageable) {
        return page2Spring(page(pageable), pageable);
    }

    public <R> org.springframework.data.domain.Page<R> pageSpringAs(Pageable pageable, Class<R> asType) {
        return page2Spring(pageAs(pageable, asType), pageable);
    }

    private static <T> org.springframework.data.domain.Page<T> page2Spring(Page<T> page, Pageable pageable) {
        return new PageImpl<>(
                page.getRecords(),
                pageable,
                page.getTotalRow()
        );
    }

    public SpringPageableQueryChain<T> orderBy(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            String property = camelCase2UnderLine(order.getProperty());
            boolean asc = order.isAscending();
            super.orderBy(property, asc);
        });
        return this;
    }

    @Override
    public BaseMapper<T> baseMapper() {
        return queryChain.baseMapper();
    }

    @Override
    public QueryWrapper toQueryWrapper() {
        return this;
    }

    @Override
    public String toSQL() {
        TableInfo tableInfo = TableInfoFactory.ofMapperClass(baseMapper().getClass());
        CPI.setFromIfNecessary(this, tableInfo.getSchema(), tableInfo.getTableName());
        return super.toSQL();
    }

    private static String camelCase2UnderLine(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));
        
        for (int i = 1; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        
        return result.toString();
    }

    private static <T> Page<T> pageable2Page(Pageable pageable) {
        Page<T> page = Page.of(pageable.getPageNumber(), pageable.getPageSize());
        
        // 处理排序信息
        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                String property = camelCase2UnderLine(order.getProperty());
                page.addOrder(new com.mybatisflex.core.paginate.Order(
                    property, 
                    order.isAscending() ? com.mybatisflex.core.paginate.Order.Direction.ASC : com.mybatisflex.core.paginate.Order.Direction.DESC
                ));
            });
        }
        
        return page;
    }
}
