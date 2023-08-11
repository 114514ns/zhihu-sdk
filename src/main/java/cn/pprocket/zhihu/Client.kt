package cn.pprocket.zhihu

import cn.pprocket.zhihu.interceptor.CookieInterceptor
import cn.pprocket.zhihu.interceptor.EncryptInterceptor
import cn.pprocket.zhihu.`object`.Answer
import cn.pprocket.zhihu.`object`.Question
import cn.pprocket.zhihu.`object`.User
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Date
fun main(args: Array<String>) {
    val fileReader = FileReader("cookie.txt")
    Client.login(fileReader.readText())
    Client.getRecommend()
}
object Client {
    var dateFormat = SimpleDateFormat()
    var client =OkHttpClient.Builder().addInterceptor(CookieInterceptor()).addInterceptor(EncryptInterceptor()).build()
    var cookie = ""
    fun login(cookie:String): Boolean {
        dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss")
        this.cookie = cookie
        return true;
    }

    fun getRecommend():List<Answer> {
        var sendGet = sendGet("https://www.zhihu.com/api/v3/feed/topstory/recommend?action=down&page_number=2")
        var asJsonObject = JsonParser.parseString(sendGet).asJsonObject
        var list = mutableListOf<Answer>()
        var data = asJsonObject.getAsJsonArray("data").forEach{
            var target = it.asJsonObject.getAsJsonObject("target")
            val question = parseQuestion(target.getAsJsonObject("question"))
            var answer = parseAnswer(target)
            answer.question = question
            list.add(answer)
        }
        return list
    }
    fun getAnswer(questionId:Int, answerId: Long):Answer {
        val sendGet = sendGet("https://www.zhihu.com/question/${questionId}/answer/${answerId}")
        val jsonString = getJSONString(sendGet)
        val asJsonObject = JsonParser.parseString(jsonString).asJsonObject.getAsJsonObject("initialState").getAsJsonObject("entities")
        val question = parseQuestion(asJsonObject.getAsJsonObject("questions").asJsonObject)
        val answer = parseAnswer(asJsonObject.getAsJsonObject("answers"))
        answer.question = question;
        return answer

    }
    fun parseUser(userObj:JsonObject):User {
        var user = User()
        user.name = userObj.get("name").asString
        user.userId = userObj.get("id").asString
        user.avatar = try {userObj.get("avatarUrl").asString} catch (e:Exception) {userObj.get("avatar_url").asString}
        user.headline = userObj.get("headline").asString
        user.token = userObj.get("urlToken").asString
        if (userObj.getAsJsonObject("cover")!=null) {
            //说明这里的user时主页的user，不是回答里的
        }
        return user
    }
    fun parseQuestion(obj:JsonObject):Question {
        val question = Question()
        var obj1 = JsonObject()
        try {
            obj.asMap().keys.forEach{question.id = it.toLong()}
            obj.asMap().values.forEach{obj1 = it as JsonObject }
        } catch (e:Exception) {
            obj1 = obj
        }
        question.title = obj1.get("title").asString
        question.detail = obj1.get("detail").asString
        question.create = dateFormat.format(Date(obj1.get("created").asLong * 1000))
        question.answerCount = try {obj1.get("answerCount").asInt} catch (e:Exception) {0}
        val parseUser = parseUser(obj1.get("author").asJsonObject)
        question.user = parseUser
        question.visitCount = try {obj1.get("visitCount").asInt} catch (e:Exception) {0}
        return question
    }
    fun parseAnswer(obj1:JsonObject):Answer {
        val answer = Answer()
        var obj = JsonObject()
        try {
            obj1.asMap().values.forEach{obj = it as JsonObject }
            obj1.asMap().keys.forEach{answer.id = it.toLong() }
        } catch (e:Exception) {
            obj = obj1
        }
        answer.user = parseUser(obj.get("author").asJsonObject)
        answer.create = dateFormat.format(Date(obj.get(FiledDefine.get(FILED.CREATE_TIME)).asLong * 1000))
        answer.preview = obj.get("excerpt").asString
        answer.content = obj.get("content").asString
        answer.like = obj.get(FiledDefine.get(FILED.LIKE)).asInt
        return answer
    }
    fun getQuestion(questionId: Int) {
        var url = "https://www.zhihu.com/question/${questionId}"
        var response = sendGet(url)
        var asJsonObject = JsonParser.parseString(getJSONString(response)).asJsonObject
            .getAsJsonObject("initialState")
            .getAsJsonObject("entities")
            .getAsJsonObject("questions")
            .asMap().forEach {
                val question = parseQuestion(it.value.asJsonObject)
                question.id = it.key.toLong()
                println(question)
            }

    }

    fun search(keyWord:String) {
        var s = "https://www.zhihu.com/api/v4/search_v3?gk_version=gz-gaokao&t=general&q=${keyWord}&correction=1&offset=0&limit=20&filter_fields=&lc_idx=0&show_all_topics=0&search_source=Normal"
    }
    fun sendGet(url:String):String {
        val builder = Request.Builder()
        val req = builder.get().url(url).build()
        return client.newCall(req).execute().body.string()
    }
    fun getJSONString(str:String):String {
        val elementById = Jsoup.parse(str).getElementById("js-initialData")
        return elementById?.toString()?.replace("<script id=\"js-initialData\" type=\"text/json\">","")
            ?.replace("</script>","")!!
    }

}