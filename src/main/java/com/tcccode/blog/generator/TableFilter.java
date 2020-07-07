package com.tcccode.blog.generator;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @PackageName: com.tcccode.blog.generator
 * @Auther: chuan
 * @Date: 2018/10/9 15:53
 * @Description: 数据库表对应eituty生成名单设置，在 isSkipTable 方法中的能被返回false的表名都可以被生成相应的entity
 * <p>
 * Copyright (c) 2018, chuan All Rights Reserved.
 */
public class TableFilter extends MetaBuilder {
    public TableFilter(DataSource dataSource) {
        super(dataSource);
    }
    /**
     * @functionDescription: 重写isSkipTable方法，能在此方法中返回false的tableName都能生成相应的entity
     * @param: String tableName
     * @return: boolean
     * @date: 2018/10/9 下午 04:10
     */
    public boolean isSkipTable(String tableName) {
        if (tableName.equals("blog"))
            return false;
        return true;
    }

    /**
     * @functionDescription: 重写buildPrimaryKey方法，使得当主键为空时不会报出异常，而是将主键设置为null。
     * @param: TableMeta tableMeta
     * @return:
     * @date: 2018/10/9 下午 04:12
     */
    public void buildPrimaryKey(TableMeta tableMeta) throws SQLException {//重写主键方法
        ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, tableMeta.name);
        String primaryKey = "";
        int index = 0;
        while (rs.next()) {
            if (index++ > 0) {
                primaryKey += ",";
            }
            primaryKey += rs.getString("COLUMN_NAME");
        }
        if (StrKit.isBlank(primaryKey)) {//如果没有主键，写入null
            primaryKey = "null";
        }
        tableMeta.primaryKey = primaryKey;
        rs.close();
    }
    /**
     * @functionDescription: 重写getTableResult方法，使得可以支持视图的entity生成
     * @param:
     * @return: java.sql.ResultSet
     * @date: 2018/10/9 下午 04:13
     */
    protected ResultSet getTablesResultSet() throws SQLException {
        String schemaPattern = dialect instanceof OracleDialect ? dbMeta.getUserName() : null;
        return dbMeta.getTables(conn.getCatalog(), schemaPattern, null, new String[]{"TABLE","VIEW"});
    }
}
