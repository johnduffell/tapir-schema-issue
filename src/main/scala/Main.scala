import sttp.tapir.*
import sttp.tapir.json.zio.jsonBody
import zio.*
import zio.Console.printLine
import zio.json.*
import sttp.apispec.openapi.circe.yaml.*
import sttp.tapir.Schema.annotations.validate
import sttp.tapir.SchemaType.*
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

import scala.util.Try

object Testing:

  case class Wrapper(
    withFields: WithFields,
    withoutFields: WithoutFields
  )

  enum WithFields(val discrimField: String, val anotherField: String):
    case F1 extends WithFields("fieldValue1", "blah")
    case F2 extends WithFields("fieldValue2", "hello")

  enum WithoutFields:
    case W1, W2


  @main
  def actual(): Unit = {

    given Schema[WithFields] = Schema.derived

    given JsonCodec[WithFields] = DeriveJsonCodec.gen

    given Schema[WithoutFields] = Schema.derived

    given JsonCodec[WithoutFields] = DeriveJsonCodec.gen

    given Schema[Wrapper] = Schema.derived

    given JsonCodec[Wrapper] = DeriveJsonCodec.gen

    val endpoints = endpoint
      .get
      .in("blahblah")
      .out(jsonBody[Wrapper])

    val docs = OpenAPIDocsInterpreter().toOpenAPI(endpoints, "my API", "0.0.1")
    val yaml = docs.toYaml


    println(yaml)
    println("----")

    println(WithFields.F1.toJson)
    println("----")
    println(WithoutFields.W1.toJson)
    println("----")

  }


  @main
  def ideal(): Unit = {


    // uses a field "discrimField" as a discriminator
    given JsonCodec[WithFields] = JsonCodec[Map[String, String]].transformOrFail[WithFields](
      _.get("discrimField").toRight("no discriminator in object").flatMap(discrimField => Try(WithFields.valueOf(discrimField)).toEither.left.map(_.toString)),
      c => Map("discrimField" -> c.discrimField, "anotherField" -> c.anotherField)
    )

    given Schema[WithFields] =
      Schema[WithFields](
        SchemaType.SProduct[WithFields](
          List(
            SProductField(FieldName("discrimField"), Schema(SString()), (c: WithFields) => None/*not sure what this is*/),
            SProductField(FieldName("anotherField"), Schema(SString()), (c: WithFields) => None),
          )
        )
      )


    given JsonCodec[WithoutFields] = summon[JsonCodec[String]].transform[WithoutFields]({
      case "W1" => WithoutFields.W1
      case "W2" => WithoutFields.W2
    }, _.toString)

    given Schema[WithoutFields] =
      Schema.derivedEnumeration().name(None).validate(Validator.enumeration(
        List[Testing.WithoutFields](WithoutFields.W1, WithoutFields.W2),
        tu => Some(tu.toString)
      ))

    given Schema[Wrapper] = Schema.derived

    given JsonCodec[Wrapper] = DeriveJsonCodec.gen

    val endpoints = endpoint
      .get
      .in("blahblah")
      .out(jsonBody[Wrapper])

    val docs = OpenAPIDocsInterpreter().toOpenAPI(endpoints, "my API", "0.0.1")
    val yaml = docs.toYaml


    println(yaml)
    println("----")

    println(WithFields.F1.toJson)
    println("----")
    println(WithoutFields.W1.toJson)
    println("----")

  }
