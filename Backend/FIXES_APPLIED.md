# PostgreSQL Prepared Statement Error - Fixes Applied

## Problem
**Error**: `org.hibernate.exception.GenericJDBCException: JDBC exception executing SQL [ERROR: prepared statement "S_1" does not exist]`

This error occurred when Hibernate tried to lazily load the `Category` entity after executing the main query for media resources. The prepared statement became invalid due to connection pool timeout or connection invalidation.

## Root Causes Identified
1. **Missing HikariCP Configuration**: No specific connection pool settings, leading to default timeouts that were too aggressive
2. **Lazy Loading of Category**: The `@ManyToOne` relationship on `Category` was using default lazy loading, causing a second query after the connection might have been closed
3. **Improper Query Generation**: Repository methods using derived query names didn't properly reference nested properties in the Category entity

## Solutions Applied

### 1. Updated `application.yml` - HikariCP Connection Pool Configuration
**File**: `src/main/resources/application.yml`

Added comprehensive HikariCP connection pool settings:
```yaml
hikari:
  maximum-pool-size: 20          # Max connections in pool
  minimum-idle: 5                 # Min idle connections to maintain
  connection-timeout: 30000       # 30 seconds to get connection
  idle-timeout: 600000            # 10 minutes before closing idle connections
  max-lifetime: 1800000           # 30 minutes max lifetime
  auto-commit: true               # Enable auto-commit
  connection-test-query: SELECT 1 # Test query to validate connections
```

**Benefits**:
- Prevents premature connection closure
- Validates connections before use
- Maintains adequate pool size
- Better handles long-running queries

### 2. Enhanced Hibernate Configuration
**File**: `src/main/resources/application.yml`

Added optimized Hibernate/JPA properties:
```yaml
hibernate:
  jdbc:
    batch_size: 20       # Batch SQL statements for better performance
    fetch_size: 50       # Optimize result set fetching
  order_inserts: true    # Order insert statements
  order_updates: true    # Order update statements
```

**Benefits**:
- Better performance with batched operations
- Optimized database communication

### 3. Fixed Lazy Loading in MediaResource Entity
**File**: `src/main/java/com/volodymyrchikh/abitandstudhelp/domain/MediaResource.java`

Changed the Category relationship from lazy to eager loading:
```java
@ManyToOne(fetch = FetchType.EAGER)  // Changed from default lazy loading
private Category category;
```

**Benefits**:
- Category is loaded with MediaResource in a single query
- Eliminates the need for a second query that causes the prepared statement error
- Prevents connection timeout issues with lazy loading

### 4. Updated MediaResourceRepository with Explicit Queries
**File**: `src/main/java/com/volodymyrchikh/abitandstudhelp/repository/MediaResourceRepository.java`

Replaced derived query methods with explicit @Query annotations:
```java
@Query("SELECT m FROM MediaResource m WHERE m.category.name = :categoryName")
Page<MediaResource> findAllByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

@Query("SELECT m FROM MediaResource m WHERE m.category.name = :categoryName AND m.type = :type")
Page<MediaResource> findAllByCategoryNameAndType(@Param("categoryName") String categoryName, @Param("type") ResourceType type, Pageable pageable);

@Query("SELECT m FROM MediaResource m WHERE m.type = :type")
Page<MediaResource> findAllByType(@Param("type") ResourceType type, Pageable pageable);
```

**Benefits**:
- Explicit queries prevent misinterpretation of derived method names
- Proper syntax for accessing nested entity properties
- Better control over query execution

### 5. Updated Service Layer
**File**: `src/main/java/com/volodymyrchikh/abitandstudhelp/service/impl/MediaResourceServiceImpl.java`

Updated `getLatest()` method to use PageRequest:
```java
public List<MediaResourceResponse> getLatest() {
    return repository.findTop5ByOrderByCreatedAtDesc(PageRequest.of(0, 5))
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
}
```

**Benefits**:
- Proper pagination handling
- Cleaner separation of concerns

## Testing Recommendations

1. **Test Category Filtering**:
   ```
   GET /api/media-resources?categoryName=Mathematics
   ```

2. **Test Combined Filtering**:
   ```
   GET /api/media-resources?categoryName=Mathematics&type=VIDEO
   ```

3. **Test Type Filtering**:
   ```
   GET /api/media-resources?type=PDF
   ```

4. **Test Latest Resources**:
   ```
   GET /api/media-resources/latest
   ```

5. **Load Testing**: Test with multiple concurrent requests to ensure the connection pool handles load properly

## Performance Improvements

1. **Reduced Database Round Trips**: Eager loading eliminates the second query for Category
2. **Better Connection Management**: HikariCP configuration prevents connection starvation
3. **Optimized Query Execution**: Explicit queries reduce overhead of dynamic query generation

## Future Recommendations

1. **Consider DTOs**: For complex queries, consider using DTOs with @Query instead of entity mapping to reduce unnecessary field fetching
2. **Monitor Connection Pool**: Add monitoring to track connection pool usage
3. **Add Caching**: Consider caching Category entities since they're frequently accessed
4. **Query Performance**: Monitor slow queries and add appropriate indexes on `media_resources.category_id` and `categories.name`

## Configuration Summary

| Parameter | Value | Purpose |
|-----------|-------|---------|
| maximum-pool-size | 20 | Maximum concurrent connections |
| minimum-idle | 5 | Minimum idle connections to maintain |
| connection-timeout | 30s | Timeout for obtaining a connection |
| idle-timeout | 10min | Close idle connections after 10 minutes |
| max-lifetime | 30min | Close any connection after 30 minutes |
| connection-test-query | SELECT 1 | Validate connections are alive |
| jdbc.batch_size | 20 | Batch database operations |
| jdbc.fetch_size | 50 | Prefetch this many rows |

