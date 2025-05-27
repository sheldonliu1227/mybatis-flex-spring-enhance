# MyBatis-Flex Spring Enhance

[![Maven Central](https://img.shields.io/maven-central/v/com.sheldon/mybatis-flex-spring-enhance.svg)](https://search.maven.org/search?q=g:com.sheldon%20AND%20a:mybatis-flex-spring-enhance)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## 简介

MyBatis-Flex Spring Enhance 是一个为 [MyBatis-Flex](https://mybatis-flex.com/) 提供 Spring 集成增强功能的扩展库。主要目标是让 MyBatis-Flex 能够更好地与 Spring 生态系统协同工作，特别是与 Spring Data 的分页功能无缝集成。

## 特性

- 支持 Spring Data 的 `Pageable` 接口进行分页查询
- 提供 MyBatis-Flex 的 `Page` 与 Spring Data 的 `Page` 之间的转换
- 保持 MyBatis-Flex 的链式查询风格，同时兼容 Spring Data 的分页接口

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.sheldon</groupId>
    <artifactId>mybatis-flex-spring-enhance</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- MyBatis-Flex 核心依赖 -->
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-core</artifactId>
    <version>1.9.7</version>
</dependency>

<!-- Spring Data Commons 依赖 -->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>3.4.2</version>
</dependency>
```

### 使用示例

```java
import com.mybatisflex.core.query.QueryChain;
import com.sheldon.mybatis.flex.spring.enhance.pageable.SpringPageableQueryChain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

// 假设有一个 User 实体和对应的 Mapper
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 使用 Spring Data 的 Pageable 进行分页查询
    public Page<User> findUsersByName(String name, Pageable pageable) {
        // 创建 MyBatis-Flex 的查询链
        QueryChain<User> queryChain = QueryChain.of(userMapper)
                .where(USER.NAME.like(name + "%"));

        // 转换为支持 Spring Pageable 的查询链
        return SpringPageableQueryChain.of(queryChain)
                .pageSpring(pageable);
    }

    // 使用 As 方法进行结果转换
    public Page<UserDTO> findUserDTOsByName(String name, Pageable pageable) {
        QueryChain<User> queryChain = QueryChain.of(userMapper)
                .where(USER.NAME.like(name + "%"));

        return SpringPageableQueryChain.of(queryChain)
                .pageSpringAs(pageable, UserDTO.class);
    }
}
```

## API 说明

### SpringPageableQueryChain

`SpringPageableQueryChain` 是核心类，它扩展了 MyBatis-Flex 的查询链，添加了对 Spring Data 分页的支持：

- `of(QueryChain<T> queryChain)`: 创建一个新的 SpringPageableQueryChain 实例
- `page(Pageable pageable)`: 使用 Spring 的 Pageable 进行分页查询，返回 MyBatis-Flex 的 Page
- `pageAs(Pageable pageable, Class<R> asType)`: 分页查询并转换结果类型
- `pageSpring(Pageable pageable)`: 分页查询并返回 Spring Data 的 Page
- `pageSpringAs(Pageable pageable, Class<R> asType)`: 分页查询，转换结果类型，并返回 Spring Data 的 Page

## 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## 贡献

欢迎提交 Issue 或 Pull Request 来帮助改进这个项目。

## 联系方式

- 作者：Sheldon Liu
- 邮箱：sheldonliu1227@gmail.com
- GitHub：[https://github.com/sheldonliu1227/mybatis-flex-spring-enhance](https://github.com/sheldonliu1227/mybatis-flex-spring-enhance)
