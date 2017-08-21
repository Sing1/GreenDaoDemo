package sing.greendao.db.impl;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import sing.greendao.db.manager.BaseDao;
import sing.greendao.db.UserDao;
import sing.greendao.demo.User;

public class UserDaoImpl extends BaseDao<User> {
    public UserDaoImpl(Context context) {
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