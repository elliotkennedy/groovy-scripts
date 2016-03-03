import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
@Grab(group='org.apache.camel', module='camel-core', version='2.16.2')
@Grab(group='org.apache.camel', module='camel-groovy', version='2.16.2')
@Grab(group='org.apache.camel', module='camel-aws', version='2.16.2')
@Grab(group='com.amazonaws', module='aws-java-sdk', version='1.10.56')

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClient
import org.apache.camel.component.aws.sqs.SqsComponent

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.spi.Registry

def sendMessages() {

    def AmazonSQS client = new AmazonSQSClient();
    client.setRegion(Region.getRegion(Regions.EU_WEST_1))

    Registry registry = new SimpleRegistry();
    registry.put("amazonClient", client)

    def camelContext = new DefaultCamelContext(registry)

    camelContext.addComponent("sqs", new SqsComponent(camelContext))

    def producer = camelContext.createProducerTemplate()

    println("go")

    def numberOfTestMessages = 10000;

    for (int i = 1; i <= numberOfTestMessages; i++) {

        String productXml = """<voy:product xmlns:voy="http://xml.gateway.voyager-wms.net/voyager-gateway">
                                  <voy:product_id>$i</voy:product_id>
                                  <voy:barcode>$i</voy:barcode>
                                </voy:product>"""

        producer.sendBody("aws-sqs://elken-dev-gateway-product-request?amazonSQSClient=#amazonClient", productXml)
        println(i)
    }

    camelContext.stop()
}

sendMessages()