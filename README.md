greenDAO是一个将对象映射到SQLite数据库中的轻量且快速的ORM解决方案。
关于greenDAO可以看官网 [官网地址](http://greenrobot.org/greendao/)  

[github地址](https://github.com/greenrobot/greenDAO)

[原文链接](https://sing1.github.io/2016/08/18/GreenDao%E5%85%A5%E9%97%A8%E5%9F%BA%E7%A1%80/)


greenDAO 优势

* 一个精简的库
* 性能最大化
* 内存开销最小化
* 易于使用的 APIs
* 对 Android 进行高度优化

首先，我们先简单集成 greenDAO:
#### 1、在项目的 build.gradle 中加入远程仓库及插件：

```XML
buildscript {
    repositories {
        jcenter()
        mavenCentral() // add repository
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.3'
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.2' // add plugin
    }
}
```
#### 2、在 module 中的 build.gradle 中顶部引入插件，然后加入 library 依赖：

```XML
apply plugin: 'com.android.application'
apply plugin: 'org.greenrobot.greendao' // apply plugin

android {
    ...
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:26.+'

    compile 'org.greenrobot:greendao:3.2.0' // add library
}
```
然后我们`Sync Now`之后就集成完成，当然我们还需要配置一下相关路径
#### 3、自定义路径
在 module 中的 build.gradle 中最外层加入如下：

```XML
greendao {
    schemaVersion 1
    daoPackage 'sing.greendao.db'
    targetGenDir 'src/main/java'
}
```
*  schemaVersion，指定数据库schema版本号，升级等操作会用到
*  daoPackage，dao的包名，包名默认是entity所在的包
*  targetGenDir，生成数据库文件的目录

#### 4、创建一个实体类 User

```JAVA
@Entity
public class User  {

    @Id
    private Long id;

    @NotNull
    private String name;
    private int age;
}
```
然后我们在工具栏中找到 `Build`-->`Make Module 'app'`，执行之后可以看到在我们指定的路径下会生成3个类：
![](http://oupjofqw3.bkt.clouddn.com/android_greendao_0001.png)

#### 5、greenDAO的使用
首先在 Application 中初始化并提供 DaoSession 的获取方法：

```JAVA
public class App extends Application {

    private DaoSession daoSession;
    
    @Override
    public void onCreate() {
        super.onCreate();

        // greenDAO 3.0之后提供了本地数据加密，但加密会增加APK体积，视情况而选择
        // 加密写法
        // DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("super-secret");

        // 不加密写法
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();  
        
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
``` 

当我们使用的时候，在 Acticity 中首先要通过 DaoSession 获取到 UserDao：

```JAVA
DaoSession daoSession = ((App) getApplication()).getDaoSession();
UserDao dao = daoSession.getUserDao();
```
* 增加数据：

```JAVA
User user = new User();
user.setAge(Integer.valueOf(age));
user.setName(name);
dao.insert(user);
```
* 删除数据：

```JAVA
User user = list.get(position);
Long noteId = user.getId();
dao.deleteByKey(noteId);
// dao.delete(user);
```
* 修改数据：

```JAVA
User user = list.get(position);
user.setName(name);
user.setAge(Integer.valueOf(age));
dao.update(user);
```
* 查询数据：

```JAVA
List<User> notes = notesQuery.list();
List<User> notes = dao.loadAll();
List<User> notes = dao.load(key);
```

#### 6、greenDAO中的注解

* @Entity 定义实体  
　　@nameInDb 在数据库中的名字，如不写则为实体中类名  
　　@indexes 索引  
　　@createInDb 是否创建表，默认为true,false时不创建  
　　@schema 指定架构名称为实体  
　　@active 无论是更新生成都刷新
* @Id
* @NotNull 不为null
* @Unique 唯一约束
* @ToMany 一对多
* @OrderBy 排序
* @ToOne 一对一
* @Transient 不存储在数据库中
* @generated 由greendao产生的构造函数或方法

#### 7、API

```JAVA
void 	attachEntity(T entity)：
	
long 	count()：获取数据库中数据的数量
	
// 数据删除相关
void 	delete(T entity)：从数据库中删除给定的实体
void 	deleteAll() ：删除数据库中全部数据
void 	deleteByKey(K key)：从数据库中删除给定Key所对应的实体
void 	deleteByKeyInTx(java.lang.Iterable<K> keys)：使用事务操作删除数据库中给定的所有key所对应的实体
void 	deleteByKeyInTx(K... keys)：使用事务操作删除数据库中给定的所有key所对应的实体
void 	deleteInTx(java.lang.Iterable<T> entities)：使用事务操作删除数据库中给定实体集合中的实体
void 	deleteInTx(T... entities)：使用事务操作删除数据库中给定的实体
	
// 数据插入相关
long 	insert(T entity)：将给定的实体插入数据库
void 	insertInTx(java.lang.Iterable<T> entities)：使用事务操作，将给定的实体集合插入数据库
void 	insertInTx(java.lang.Iterable<T> entities, boolean setPrimaryKey)：使用事务操作，将给定的实体集合插入数据库，并设置是否设定主键
void 	insertInTx(T... entities)：将给定的实体插入数据库
long 	insertOrReplace(T entity)：将给定的实体插入数据库，若此实体类存在，则覆盖
void 	insertOrReplaceInTx(java.lang.Iterable<T> entities)：使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖
void 	insertOrReplaceInTx(java.lang.Iterable<T> entities, boolean setPrimaryKey)：使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖并设置是否设定主键
void 	insertOrReplaceInTx(T... entities)：使用事务操作，将给定的实体插入数据库，若此实体类存在，则覆盖
long 	insertWithoutSettingPk(T entity)：将给定的实体插入数据库,但不设定主键
	
// 新增数据插入相关API
void 	save(T entity)：将给定的实体插入数据库，若此实体类存在，则更新
void 	saveInTx(java.lang.Iterable<T> entities)：将给定的实体插入数据库，若此实体类存在，则更新
void 	saveInTx(T... entities)：使用事务操作，将给定的实体插入数据库，若此实体类存在，则更新
	
// 加载相关
T 	load(K key)：加载给定主键的实体
java.util.List<T> 	loadAll()：加载数据库中所有的实体
protected java.util.List<T> 	loadAllAndCloseCursor(android.database.Cursor cursor) ：从cursor中读取、返回实体的列表，并关闭该cursor
protected java.util.List<T> 	loadAllFromCursor(android.database.Cursor cursor)：从cursor中读取、返回实体的列表
T 	loadByRowId(long rowId) ：加载某一行并返回该行的实体
protected T 	loadUnique(android.database.Cursor cursor) ：从cursor中读取、返回唯一实体
protected T 	loadUniqueAndCloseCursor(android.database.Cursor cursor) ：从cursor中读取、返回唯一实体，并关闭该cursor

//更新数据
void 	update(T entity) ：更新给定的实体
protected void 	updateInsideSynchronized(T entity, DatabaseStatement stmt, boolean lock) 
protected void 	updateInsideSynchronized(T entity, android.database.sqlite.SQLiteStatement stmt, boolean lock) 
void 	updateInTx(java.lang.Iterable<T> entities) ：使用事务操作，更新给定的实体
void 	updateInTx(T... entities)：使用事务操作，更新给定的实体
```
#### 8、封装
我们换一种写法，在第4部做完之后不在 Application 中初始化，而在使用的时候进行初始化，我们创建 DbManager.java 来进行管理：

```JAVA
/**
 * 进行数据库的管理
 * 1.创建数据库
 * 2.创建数据库表
 * 3.对数据库进行增删查改
 * 4.对数据库进行升级
 */
public class DbManager {
    private static final String DB_NAME = "dataa.db";//数据库名称
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
```
然后我们创建 BaseDao.java 来封装使用的增、删、改、查，而且在他的继承类中可以方便扩展：

```JAVA
public class BaseDao<T>{
    public static final String TAG = BaseDao.class.getSimpleName();
    public static final boolean DUBUG = true;
    public DbManager manager;
    public DaoSession daoSession;

    public BaseDao(Context context) {
        manager = DbManager.getInstance().init(context);
        daoSession = manager.getDaoSession();
        manager.setDebug(DUBUG);
    }

    /**************************数据库插入操作***********************/
    // 插入单个对象
    public boolean insert(T object){
        boolean flag = false;
        try {
            flag = manager.getDaoSession().insert(object) != -1 ? true:false;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return flag;
    }

    // 插入多个对象，并开启新的线程
    public boolean insert(final List<T> objects){
        boolean flag = false;
        if (null == objects || objects.isEmpty()){
            return false;
        }
        try {
            manager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        manager.getDaoSession().insertOrReplace(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            flag = false;
        }finally {
//            manager.CloseDataBase();
        }
        return flag;
    }

    /**************************数据库更新操作***********************/
    // 以对象形式进行数据修改,其中必须要知道对象的主键ID
    public void  update(T object){
        if (null == object){
            return ;
        }
        try {
            manager.getDaoSession().update(object);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    // 批量更新数据
    public void update(final List<T> objects, Class clss){
        if (null == objects || objects.isEmpty()){
            return;
        }
        try {
            daoSession.getDao(clss).updateInTx(new Runnable() {
                @Override
                public void run() {
                    for(T object:objects){
                        daoSession.update(object);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**************************数据库删除操作***********************/
    // 删除某个数据库表
    public boolean deleteTable(Class clss){
        boolean flag = false;
        try {
            manager.getDaoSession().deleteAll(clss);
            flag = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            flag = false;
        }
        return flag;
    }

    // 删除某个对象
    public void delete(T object){
        try {
            daoSession.delete(object);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    // 异步批量删除数据
    public boolean delete(final List<T> objects, Class clss){
        boolean flag = false;
        if (null == objects || objects.isEmpty()){
            return false;
        }
        try {
            daoSession.getDao(clss).deleteInTx(new Runnable() {
                @Override
                public void run() {
                    for(T object:objects){
                        daoSession.delete(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            flag = false;
        }
        return flag;
    }

    // 根据主键ID来删除
    public void deleteById(long id,Class clss){
        daoSession.getDao(clss).deleteByKey(id);
    }

    /**************************数据库查询操作***********************/

    // 获得某个表名
    public String getTableName(Class object){
        return daoSession.getDao(object).getTablename();
    }

    // 查询某个ID的对象是否存在
    public boolean isExitObject(long id, Class object){
        QueryBuilder<T> qb = (QueryBuilder<T>) daoSession.getDao(object).queryBuilder();
        qb.where(UserDao.Properties.Id.eq(id));// 这里需要继承类来实现，这是例子
        long length = qb.buildCount().count();
        return length>0 ? true:false;
    }

    // 根据主键ID来查询
    public T queryById(long id,Class object){
        return (T) daoSession.getDao(object).loadByRowId(id);
    }

    // 查询某条件下的对象
    public List<T> queryObject(Class object,String where,String...params){
        Object obj = null;
        List<T> objects = null;
        try {
            obj = daoSession.getDao(object);
            if (null == obj){
                return null;
            }
            objects = daoSession.getDao(object).queryRaw(where,params);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return objects;
    }
    // 查询所有对象
    public List<T> queryAll(Class object){
        List<T> objects = null;
        try {
            objects = (List<T>) daoSession.getDao(object).loadAll();
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        return objects;
    }

    /***************************关闭数据库*************************/
    // 关闭数据库一般在Odestory中使用
    public void closeDataBase(){
        manager.closeDataBase();
    }
}
```
之后我们所以的实现类都继承此类即可，需要重写的部分重写，比如简单的：

```JAVA
public class UserDaoIml extends BaseDao<User> {
    public UserDaoIml(Context context) {
        super(context);
    }

    @Override // 重写了此方法
    public boolean isExitObject(long id, Class object) {
        QueryBuilder<User> qb = (QueryBuilder<User>) daoSession.getDao(object).queryBuilder();
        qb.where(UserDao.Properties.Id.eq(id));
        long length = qb.buildCount().count();
        return length > 0 ? true : false;
    }
}
```
然后我们就可以在 Activity 中使用所以的方法：

```JAVA
UserDaoIml userDaoIml = new UserDaoIml(this);

userDaoIml.insert(user);
userDaoIml.delete(user);
userDaoIml.update(user);
userDaoIml.queryAll(User.class)
```
而且我们也封装了事务处理，支持批量操作，是不是很方便？
![](http://oupjofqw3.bkt.clouddn.com/android_greendao_0002.gif)  

由上我们增加了3条数据，然后我们将数据库导出查看：

![](http://oupjofqw3.bkt.clouddn.com/android_greendao_0003.png)

然后我们演示一下更新和删除操作

![](http://oupjofqw3.bkt.clouddn.com/android_greendao_0004.gif)
