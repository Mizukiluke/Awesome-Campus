package cn.edu.jxnu.awesome_campus.database.dao.education;

import com.squareup.okhttp.Headers;

import java.io.IOException;
import java.util.List;

import cn.edu.jxnu.awesome_campus.InitApp;
import cn.edu.jxnu.awesome_campus.database.dao.DAO;
import cn.edu.jxnu.awesome_campus.database.spkey.EducationStaticKey;
import cn.edu.jxnu.awesome_campus.event.EVENT;
import cn.edu.jxnu.awesome_campus.event.EventModel;
import cn.edu.jxnu.awesome_campus.model.education.CourseScoreModel;
import cn.edu.jxnu.awesome_campus.model.education.ExamTimeModel;
import cn.edu.jxnu.awesome_campus.support.htmlprase.ExamTimePrase;
import cn.edu.jxnu.awesome_campus.support.urlconfig.Urlconfig;
import cn.edu.jxnu.awesome_campus.support.utils.common.SPUtil;
import cn.edu.jxnu.awesome_campus.support.utils.net.NetManageUtil;
import cn.edu.jxnu.awesome_campus.support.utils.net.callback.StringCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by MummyDing on 16-2-2.
 * GitHub: https://github.com/MummyDing
 * Blog: http://blog.csdn.net/mummyding
 */
public class ExamTimeDAO implements DAO<ExamTimeModel> {
    public static final String TAG="ExamTimeDAO";
    @Override
    public boolean cacheAll(List<ExamTimeModel> list) {
        return false;
    }

    @Override
    public boolean clearCache() {
        return false;
    }

    @Override
    public void loadFromCache() {

    }

    @Override
    public void loadFromNet() {
        SPUtil spu = new SPUtil(InitApp.AppContext);
        String cookies = "ASP.NET_SessionId=" +
                spu.getStringSP(EducationStaticKey.SP_FILE_NAME, EducationStaticKey.BASE_COOKIE) +
                ";JwOAUserSettingNew=" +
                spu.getStringSP(EducationStaticKey.SP_FILE_NAME, EducationStaticKey.SPECIAL_COOKIE);
        NetManageUtil.get(Urlconfig.ExamTime_URL)
                .addHeader("Cookie", cookies)
                .addTag(TAG).enqueue(new StringCallback() {
            @Override
            public void onSuccess(String result, Headers headers) {
                ExamTimePrase myPrase = new ExamTimePrase(result);
                List list = myPrase.getEndList();
                if (list != null) {
                    // 缓存数据
                    cacheAll(list);
                    //发送获取成功消息
                    EventBus.getDefault().post(new EventModel<CourseScoreModel>(EVENT.COURSE_SCORE_REFRESH_SUCCESS, list));
                } else {
                    //发送获取失败消息
                    EventBus.getDefault().post(new EventModel<CourseScoreModel>(EVENT.COURSE_SCORE_REFRESH_FAILURE));
                }
            }

            @Override
            public void onFailure(IOException e) {
                EventBus.getDefault().post(new EventModel<CourseScoreModel>(EVENT.COURSE_SCORE_REFRESH_FAILURE));
            }
        });

    }

}
