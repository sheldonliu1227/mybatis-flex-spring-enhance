package com.sheldon.mybatis.flex.spring.enhance.pageable;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class SpringPageableQueryChain<T>  extends QueryWrapperAdapter<SpringPageableQueryChain<T>> implements MapperQueryChain<T> {
    private final QueryChain<T> queryChain;

    private SpringPageableQueryChain(QueryChain<T> queryChain) {
        this.queryChain = queryChain;
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

    private static <T> Page<T> pageable2Page(Pageable pageable) {
        return Page.of(pageable.getPageNumber(), pageable.getPageSize());
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
}
