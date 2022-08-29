package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DraftDetailedTests {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenDeserialized_ThenPopulates() {

        var json = "{" +
          "        \"Name\": \"adraftname\"," +
          "        \"DraftId\": \"adraftid\"," +
          "        \"Configuration\": {" +
          "          \"Id\": \"aconfigurationid\"," +
          "          \"APropertyName1\": \"avalue\"," +
          "          \"APropertyName2\": true," +
          "          \"APropertyName3\": 12," +
          "          \"APropertyName4\": 12.3," +
          "          \"ACollectionName1\": {" +
          "            \"Id\": \"acollectionname1id\"," +
          "            \"Items\": [" +
          "              {" +
          "                \"Id\": \"acollectionname1itemid1\"," +
          "                \"APropertyName1\": \"avalue\"," +
          "                \"APropertyName2\": true," +
          "                \"APropertyName3\": 12," +
          "                \"APropertyName4\": 12.3," +
          "                \"AnElementName1\": {" +
          "                  \"Id\": \"anelementid1\"," +
          "                  \"ACollectionName2\": {" +
          "                    \"Id\": \"acollectionname2id\"," +
          "                    \"Items\": [" +
          "                      {" +
          "                        \"Id\": \"acollectionname2itemid1\"," +
          "                        \"APropertyName1\": \"avalue1\"" +
          "                      }," +
          "                      {" +
          "                        \"Id\": \"acollectionname2itemid2\"," +
          "                        \"APropertyName1\": \"avalue2\"" +
          "                      }" +
          "                    ]" +
          "                  }" +
          "                }" +
          "              }," +
          "              {" +
          "                \"Id\": \"acollectionname1itemid2\"," +
          "                \"APropertyName1\": \"avalue\"," +
          "                \"APropertyName2\": true," +
          "                \"APropertyName3\": 12," +
          "                \"APropertyName4\": 12.3," +
          "                \"AnElementName1\": {" +
          "                  \"Id\": \"anelementid1\"," +
          "                  \"ACollectionName2\": {" +
          "                    \"Id\": \"acollectionname2id\"," +
          "                    \"Items\": [" +
          "                      {" +
          "                        \"Id\": \"acollectionname2itemid1\"," +
          "                        \"APropertyName1\": \"avalue1\"" +
          "                      }," +
          "                      {" +
          "                        \"Id\": \"acollectionname2itemid2\"," +
          "                        \"APropertyName1\": \"avalue2\"" +
          "                      }" +
          "                    ]" +
          "                  }" +
          "                }" +
          "              }," +
          "            ]" +
          "          }," +
          "          \"AnElementName2\": {" +
          "            \"Id\": \"anelementname2id\"," +
          "            \"APropertyName1\": \"avalue\"," +
          "            \"APropertyName2\": true," +
          "            \"APropertyName3\": 12," +
          "            \"APropertyName4\": 12.3" +
          "          }" +
          "        }" +
          "      }";
        var gson = new Gson();

        var result = gson.fromJson(json, DraftDetailed.class);

        assertEquals("adraftname", result.getName());
        assertEquals("adraftid", result.getId());
        var configuration = result.getRoot();
        assertEquals("aconfigurationid", configuration.getId());
        assertEquals("adraftname", configuration.getName());
        var properties = configuration.getProperties();
        assertEquals(4, properties.size());
        assertEquals("APropertyName1", properties.get("APropertyName1").getName());
        assertEquals("avalue", properties.get("APropertyName1").getValue());
        assertEquals("true", properties.get("APropertyName2").getValue());
        assertEquals("12", properties.get("APropertyName3").getValue());
        assertEquals("12.3", properties.get("APropertyName4").getValue());

        var collections = configuration.getCollections();
        assertEquals(1, collections.size());
        var collectionName1 = collections.get("ACollectionName1");
        assertEquals("acollectionname1id", collectionName1.getId());
        assertEquals("ACollectionName1", collectionName1.getName());
        var collectionName1Items = collectionName1.getCollectionItems();
        assertEquals(2, collectionName1Items.size());
        var collectionName1Item1 = collectionName1Items.get(0);
        assertEquals("acollectionname1itemid1", collectionName1Item1.getId());
        assertEquals("acollectionname1itemid2", collectionName1Items.get(1).getId());
        assertEquals("Items", collectionName1Items.get(1).getName());
        assertEquals(4, collectionName1Item1.getProperties().size());
        assertEquals("APropertyName1", collectionName1Item1.getProperty("APropertyName1").getName());
        assertEquals("avalue", collectionName1Item1.getProperty("APropertyName1").getValue());
        assertEquals("true", collectionName1Item1.getProperty("APropertyName2").getValue());
        assertEquals("12", collectionName1Item1.getProperty("APropertyName3").getValue());
        assertEquals("12.3", collectionName1Item1.getProperty("APropertyName4").getValue());

        var elementName1 = collectionName1Item1.getElement("AnElementName1");
        assertEquals("anelementid1", elementName1.getId());
        assertEquals("AnElementName1", elementName1.getName());
        assertEquals(1, elementName1.getCollections().size());

        var collectionName2 = elementName1.getCollections().get("ACollectionName2");
        assertEquals("acollectionname2id", collectionName2.getId());
        assertEquals(2, collectionName2.getCollectionItems().size());
        var collectionName2Item1 = collectionName2.getCollectionItems().get(0);
        assertEquals(1, collectionName2Item1.getProperties().size());
        assertEquals("acollectionname2itemid1", collectionName2Item1.getId());
        assertEquals("APropertyName1", collectionName2Item1.getProperty("APropertyName1").getName());
        assertEquals("avalue1", collectionName2Item1.getProperty("APropertyName1").getValue());
        var collectionName2Item2 = collectionName2.getCollectionItems().get(1);
        assertEquals(1, collectionName2Item2.getProperties().size());
        assertEquals("acollectionname2itemid2", collectionName2Item2.getId());
        assertEquals("APropertyName1", collectionName2Item2.getProperty("APropertyName1").getName());
        assertEquals("avalue2", collectionName2Item2.getProperty("APropertyName1").getValue());

        var elements = configuration.getElements();
        assertEquals(1, elements.size());
        var elementName2 = elements.get("AnElementName2");
        assertEquals("anelementname2id", elementName2.getId());
        assertEquals("AnElementName2", elementName2.getName());
        assertEquals(4, elementName2.getProperties().size());
        assertEquals("APropertyName1", elementName2.getProperty("APropertyName1").getName());
        assertEquals("avalue", elementName2.getProperty("APropertyName1").getValue());
        assertEquals("true", elementName2.getProperty("APropertyName2").getValue());
        assertEquals("12", elementName2.getProperty("APropertyName3").getValue());
        assertEquals("12.3", elementName2.getProperty("APropertyName4").getValue());
    }

}
