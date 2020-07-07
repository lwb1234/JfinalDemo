package com.tcccode.blog.generator;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.druid.DruidPlugin;
import com.tcccode.blog.common.DemoConfig;

import javax.sql.DataSource;

/**
 * @PackageName: com.tcccode.blog.generator
 * @Auther: chuan
 * @Date: 2018/10/9 15:52
 * @Description: 用于自动生成entity，生成后的entity能够通过继承即时拥有众多方便操作数据库的方法，使用更为方便。
 * 首次使用此方法时，请注意配置生成的entity路径。
 * 每当数据库变有所改动时，可以直接启动此中的main方法，即可重新生成正确的entity.
 * 如果需要修改需要生成的表，可以到TableFilter类中的isSkipTable中去增删改相应表名，再次执行此方法，entity即可更新
 * <p>
 * Copyright (c) 2018, chuan All Rights Reserved.
 */
public class EntityGenerator {
    public static DataSource getDataSource() {
        PropKit.use("jdbc.properties");
        DruidPlugin druidPlugin = DemoConfig.createDruidPlugin();
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }
    public static void main(String[] args) {
        // base model 所使用的包名
        String baseModelPackageName = "com.tcccode.entity.base";
        // base model 文件保存路径
        String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/tcccode/entity/base";

        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = "com.tcccode.entity";
        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        DataSource  dataSource= getDataSource();
        // 创建生成器
        Generator generator = new Generator(dataSource, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
        // 设置是否生成链式 setter 方法
        MetaBuilder metaBuilder = new TableFilter(dataSource);
        //请设置数据库类型(MysqlDialect,SqlServerDialect,OracleDialect,Sqlite3Dialect,AnsiSqlDialect,PostgreSqlDialect)
        metaBuilder.setDialect(new MysqlDialect());
        generator.setMetaBuilder(metaBuilder);
        generator.setGenerateChainSetter(true);
        // 添加不需要生成的表名
//		generator.addExcludedTable("adv","Announcement","AnnountChangeTmp");
        // 设置是否在 Model 中生成 dao 对象
        generator.setGenerateDaoInModel(true);
        // 设置是否生成链式 setter 方法
        generator.setGenerateChainSetter(true);
        // 设置是否生成字典文件
        generator.setGenerateDataDictionary(true);
        // 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
        generator.setRemovedTableNamePrefixes("t_");
        // 生成
        generator.generate();
    }
}
