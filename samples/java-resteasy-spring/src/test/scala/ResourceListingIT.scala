/**
 *  Copyright 2014 Reverb Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import com.wordnik.swagger.model._

import com.wordnik.swagger.core.util.{ ScalaJsonUtil, JsonSerializer }

import org.junit.runner.RunWith

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.collection.JavaConversions._

import scala.io._

@RunWith(classOf[JUnitRunner])
class ResourceListingIT extends FlatSpec with Matchers {
  it should "read a resource listing" in {
    val json = Source.fromURL("http://localhost:8002/api/api-docs").mkString
    val doc = JsonSerializer.asResourceListing(json)

    doc.apis.size should be (2)
    (doc.apis.map(api => api.path).toSet & Set("/pet", "/user")).size should be (2)
  }

  ignore should "read the resource listing in XML" in {
    val xmlString = Source.fromURL("http://localhost:8002/api/api-docs.xml").mkString
    val xml = scala.xml.XML.loadString(xmlString)
    ((xml \ "apis").map(api => (api \ "path").text).toSet & Set("/pet", "/user")).size should be (2)
  }

  it should "read the pet api description" in {
    val json = Source.fromURL("http://localhost:8002/api/api-docs/pet").mkString
    val doc = JsonSerializer.asApiListing(json)
    doc.apis.size should be (3)
    (doc.apis.map(api => api.path).toSet &
      Set("/pet/{petId}",
        "/pet/findByStatus",
        "/pet/findByTags")).size should be (3)
  }

  ignore should "read the user api with array and list data types as post data" in {
    val json = Source.fromURL("http://localhost:8002/api/api-docs/user").mkString
    val doc = JsonSerializer.asApiListing(json)
    doc.apis.size should be (3)
    (doc.apis.map(api => api.path).toSet &
      Set("/user",
        "/user/createWithArray",
        "/user/createWithList")).size should be (3)

    var param = doc.apis.filter(api => api.path == "/user/createWithList")(0).operations(0).parameters(0)
    param.dataType should be ("List[User]")
  }

  ignore should "read the pet api description in XML" in {
    val xmlString = Source.fromURL("http://localhost:8002/api/api-docs/pet").mkString
    val xml = scala.xml.XML.loadString(xmlString)

    ((xml \ "apis").map(api => (api \ "path").text).toSet &
      Set("/pet/{petId}",
        "/pet/findByStatus",
        "/pet/findByTags")).size should be (3)
  }
}
