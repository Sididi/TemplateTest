package com.redpills.correction.framework.helpers

import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.util.*


object Http {
    private val template = RestTemplate()

    init {
        val jsonHttpMessageConverter = MappingJackson2HttpMessageConverter()
        jsonHttpMessageConverter.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        template.messageConverters.add(jsonHttpMessageConverter)
    }

    fun request(method: HttpMethod, url: String, vararg args: Any): RestHttpRequest {
        return RestHttpRequest(
            template,
            String.format(url, *args),
            method
        )
    }

    fun get(url: String, vararg args: Any): RestHttpRequest {
        return RestHttpRequest(
            template,
            String.format(url, *args),
            HttpMethod.GET
        ).withContentType(MediaType.APPLICATION_JSON)
    }

    fun post(url: String, bodyObject: Any): RestHttpRequest {
        return RestHttpRequest(
            template,
            url,
            HttpMethod.POST
        ).withBody(bodyObject).withContentType(MediaType.APPLICATION_JSON)
    }

    fun put(url: String, bodyObject: Any): RestHttpRequest {
        return RestHttpRequest(
            template,
            url,
            HttpMethod.PUT
        ).withBody(bodyObject)
    }

    fun delete(url: String, vararg args: Any): RestHttpRequest {
        return RestHttpRequest(
            template,
            String.format(url, *args),
            HttpMethod.DELETE
        )
    }
}

class RestHttpRequest(
    private val template: RestTemplate,
    private val url: String,
    private val method: HttpMethod
) {
    private var headers: HttpHeaders = HttpHeaders()
    private var bodyObject: Any? = null

    fun withContentType(type: MediaType): RestHttpRequest {
        this.headers.contentType = type
        return this
    }

    fun accepts(vararg types: MediaType): RestHttpRequest {
        this.headers.accept = types.toList()
        return this
    }

    fun addHeader(key: String, value: String): RestHttpRequest {
        this.headers.set(key, value)
        return this
    }

    fun setHeaders(headers: HttpHeaders): RestHttpRequest {
        this.headers = headers
        return this
    }

    fun withBody(bodyObject: Any): RestHttpRequest {
        this.bodyObject = bodyObject
        return this
    }

    fun toResponse() {
        val body = HttpEntity(bodyObject, headers)
        /* ignore returned value */
        template.exchange(url, method, body, Any::class.java)
    }

    fun toHttpResponse(): HttpEntity<String> {
        val body = HttpEntity(bodyObject as String, headers)
        return template.exchange(url, method, body, String::class.java)
    }

    fun <T> toResponse(responseType: Class<T>): T? {
        val body = HttpEntity(bodyObject as T, headers)
        val response = template.exchange(url, method, body, responseType)
        return response.body
    }

    fun <T> toResponseList(setType: Class<Array<T>>): List<T> {
        val body = HttpEntity(bodyObject as T, headers)
        val response = template.exchange(url, method, body, setType)
        return ArrayList<T>(response.body as Collection<T>)
    }
}