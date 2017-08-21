package sing.greendao.db.manager;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import sing.greendao.db.DaoMaster;
import sing.greendao.db.DaoSession;

/**
 * 进行数据库的管理
 * 1.创建数据库
 * 2.创建数据库表
 * 3.对数据库进行增删查改
 * 4.对数据库进行升级
 */
public class DbManager {
    private static final String DB_NAME = "data.db";//数据库名称
    private volatile static DbManager mDaoManager;//多线程访问
    private static DaoMaster.DevOpenHelper mHelper;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private Context context;

    // 使用单例模式获得操作数据库的对象
    public static DbManager getInstance() {
        DbManager instance = null;
        if (mDaoManager == null) {
            synchronized (DbManager.class) {
                if (instance == null) {
                    instance = new DbManager();
                    mDaoManager = instance;
                }
            }
        }
        return mDaoManager;
    }

    // 初始化Context对象
    public DbManager init(Context context) {
        this.context = context;
        return mDaoManager;
    }

    // 判断数据库是否存在，如果不存在则创建
    public DaoMaster getDaoMaster() {
        if (null == mDaoMaster) {
            mHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    // 完成对数据库的增删查找
    public DaoSession getDaoSession() {
        if (null == mDaoSession) {
            if (null == mDaoMaster) {
                mDaoMaster = getDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    // 设置debug模式开启或关闭，默认关闭
    public void setDebug(boolean flag) {
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    // 关闭数据库
    public void closeDataBase() {
        closeHelper();
        closeDaoSession();
    }

    public void closeDaoSession() {
        if (null != mDaoSession) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }
}