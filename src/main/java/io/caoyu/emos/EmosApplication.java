package io.caoyu.emos;

import cn.hutool.core.util.StrUtil;
import io.caoyu.emos.wx.config.SystemConstants;
import io.caoyu.emos.wx.db.dao.SysConfigDao;
import io.caoyu.emos.wx.db.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@ServletComponentScan
@SpringBootApplication
@Slf4j
@EnableAsync
public class EmosApplication {
    @Value("${emos.image-folder}")
    private String imageFolder;
    @Autowired
    private SysConfigDao sysConfigDao;
    @Autowired
    private SystemConstants constants;
    public static void main(String[] args) {
        SpringApplication.run(EmosApplication.class, args);
    }
    @PostConstruct
    public void init() {
        List<SysConfig> list = sysConfigDao.selectAllParam();
        list.forEach(one -> {
            String key = one.getParamKey();
            key = StrUtil.toCamelCase(key);
            String value = one.getParamValue();
            try {
                Field field = constants.getClass().getDeclaredField(key);
                field.set(constants, value);
            } catch (Exception e) {
                //log.error("执行异常", e);
            }
        });

        new File(imageFolder).mkdirs();

    }
}
