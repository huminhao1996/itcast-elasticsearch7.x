package com.itheima.elasticsearchdemo.v6;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.itheima.elasticsearchdemo.ElasticsearchDemoApplication;
import com.itheima.elasticsearchdemo.domain.Person;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ElasticsearchDemoApplication.class)
public class ElasticsearchDemoApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() {
       /* //1.创建ES客户端对象
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
           new HttpHost(
                   "192.168.149.135",
                   9200,
                   "http"
           )
        ));*/

        String mapping = "{\n" +
                "      \"properties\" : {\n" +
                "        \"address\" : {\n" +
                "          \"type\" : \"text\",\n" +
                "          \"analyzer\" : \"ik_max_word\"\n" +
                "        },\n" +
                "        \"age\" : {\n" +
                "          \"type\" : \"long\"\n" +
                "        },\n" +
                "        \"name\" : {\n" +
                "          \"type\" : \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";

        System.out.println(JSONObject.parseObject(mapping).toJSONString());

        System.out.println(client);
    }


    /**
     * 添加索引
     * ok
     */
    @Test
    public void addIndex() throws IOException {
        /**
         * GET /itcast
         */

        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();

        //2.具体操作，获取返回值
        CreateIndexRequest createRequest = new CreateIndexRequest("itcast");
        CreateIndexResponse response = indicesClient.create(createRequest, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        System.out.println(response.isAcknowledged());

    }


    /**
     * 添加索引 并加上映射
     * ok
     */
    @Test
    public void addIndexAndMapping() throws IOException {

        /**
         * PUT /itcast
         * {
         *   "mappings": {
         *     "_doc": {
         *       "properties": {
         *         "address": {
         *           "analyzer": "ik_max_word",
         *           "type": "text"
         *         },
         *         "name": {
         *           "type": "keyword"
         *         },
         *         "age": {
         *           "type": "long"
         *         }
         *       }
         *     }
         *   }
         * }
         */

        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();
        //2.具体操作，获取返回值
        CreateIndexRequest createRequest = new CreateIndexRequest("itcast");

        //2.1 设置mappings
        String mapping = "{\n" +
                "      \"properties\": {\n" +
                "        \"address\": {\n" +
                "          \"analyzer\": \"ik_max_word\",\n" +
                "          \"type\": \"text\"\n" +
                "        },\n" +
                "        \"name\": {\n" +
                "          \"type\": \"keyword\"\n" +
                "        },\n" +
                "        \"age\": {\n" +
                "          \"type\": \"long\"\n" +
                "        }\n" +
                "      }\n" +
                "    }";

        createRequest.mapping("_doc", mapping, XContentType.JSON);

        CreateIndexResponse response = indicesClient.create(createRequest, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        System.out.println(response.isAcknowledged());


    }


    /**
     * 查询索引
     * 语法错误
     */
    @Test
    public void queryIndex() throws IOException {
        IndicesClient indices = client.indices();

        GetIndexRequest getReqeust = new GetIndexRequest();
        getReqeust.indices("movie").types("_doc");
        GetIndexResponse getIndexResponse = client.indices().get(getReqeust, RequestOptions.DEFAULT);


//        GetRequest get = new GetRequest("movie", "text", "p8B0kIEBh2ToMB2wjZlO");
//        GetResponse response = client.get(get, RequestOptions.DEFAULT);


        //获取结果
//        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = response.getMappings();

//        for (String key : mappings.keySet()) {
//            System.out.println(key+":" + mappings.get(key).getSourceAsMap());
//        }

    }


    /**
     * 删除索引
     * ok
     */
    @Test
    public void deleteIndex() throws IOException {
        /**
         * DELETE itcast
         */

        IndicesClient indices = client.indices();

        DeleteIndexRequest deleteRequest = new DeleteIndexRequest("itcast");
        AcknowledgedResponse response = indices.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(response.isAcknowledged());

    }

    /**
     * 判断索引是否存在
     * ok
     */
    @Test
    public void existIndex() throws IOException {
        IndicesClient indices = client.indices();

        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices("itcast");

        boolean exists = indices.exists(getIndexRequest, RequestOptions.DEFAULT);

        System.out.println(exists);

    }


    /**
     * 添加文档,使用map作为数据
     * ok
     */
    @Test
    public void addDoc() throws IOException {
        //数据对象，map
        Map data = new HashMap();
        data.put("address", "北京昌平");
        data.put("name", "大胖");
        data.put("age", 20);


        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast", "_doc", "1").source(data);
        //添加数据，获取结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        //打印响应结果
        System.out.println(response.getId());


    }


    /**
     * 添加文档,使用对象作为数据
     * ok
     */
    @Test
    public void addDoc2() throws IOException {
        //数据对象，javaObject
        Person p = new Person();
        p.setId("2");
        p.setName("小胖2222");
        p.setAge(30);
        p.setAddress("陕西西安");

        //将对象转为json
        String data = JSON.toJSONString(p);

        //1.获取操作文档的对象
        IndexRequest request = new IndexRequest("itcast","_doc",p.getId())
                .source(data, XContentType.JSON);
        //添加数据，获取结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        //打印响应结果
        System.out.println(response.getId());
    }


    /**
     * 修改文档：添加文档时，如果id存在则修改，id不存在则添加
     */
    @Test
    public void updateDoc() throws IOException {

    }


    /**
     * 根据id查询文档
     * ok
     */
    @Test
    public void findDocById() throws IOException {
        GetRequest getReqeust = new GetRequest("itcast","_doc","1");
        GetResponse response = client.get(getReqeust, RequestOptions.DEFAULT);
        //获取数据对应的json
        System.out.println(response.getSourceAsString());

    }


    /**
     * 根据id删除文档
     * ok
     */
    @Test
    public void delDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("itcast","_doc","1");
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response.getId());

    }

}
