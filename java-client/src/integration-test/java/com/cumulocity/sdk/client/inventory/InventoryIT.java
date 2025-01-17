/*
 * Copyright (C) 2013 Cumulocity GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.cumulocity.sdk.client.inventory;

import static com.cumulocity.rest.representation.builder.RestRepresentationObjectMother.anMoRefRepresentationLike;
import static com.cumulocity.rest.representation.builder.RestRepresentationObjectMother.anMoRepresentationLike;
import static com.cumulocity.rest.representation.builder.SampleManagedObjectReferenceRepresentation.MO_REF_REPRESENTATION;
import static com.cumulocity.rest.representation.builder.SampleManagedObjectRepresentation.MO_REPRESENTATION;
import static com.cumulocity.sdk.client.common.SdkExceptionMatcher.sdkException;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import c8y.ThreePhaseElectricitySensor;

import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.builder.ManagedObjectRepresentationBuilder;
import com.cumulocity.rest.representation.inventory.ManagedObjectCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceCollectionRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.common.JavaSdkITBase;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class InventoryIT extends JavaSdkITBase {

    private static final int NOT_FOUND = 404;

    private InventoryApi inventory;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        inventory = platform.getInventoryApi();
        platform.setRequireResponseBody(true);
    }

    @Test
    public void createManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().build();

        // When
        ManagedObjectRepresentation created = inventory.create(rep);

        // Then
        assertNotNull(created.getId());
        assertNotNull(created.getSelf());
        assertThat(created, not(sameInstance(rep)));
    }

    @Test
    public void createManagedObjectWithoutResponseBody() throws Exception {
        // Given
        platform.setRequireResponseBody(false);
        ManagedObjectRepresentation rep = aSampleMo().build();

        // When
        ManagedObjectRepresentation created = inventory.create(rep);

        // Then
        assertNotNull(created.getId());
        assertNull(created.getSelf());
        assertThat(created, sameInstance(rep));
    }

    @Test
    public void createManagedObjectWithCoordinateFragment() throws Exception {
        // Given
        Coordinate coordinate = new Coordinate(100.0, 10.0);
        ManagedObjectRepresentation rep = aSampleMo().with(coordinate).build();

        // When
        ManagedObjectRepresentation result = inventory.create(rep);

        // Then
        assertThat(result.getId(), not(nullValue()));
        Coordinate fragment = result.get(Coordinate.class);
        assertThat(fragment, is(coordinate));
    }

    @Test
    public void createManagedObjectWithThreePhaseElectricitySensor() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().with(new ThreePhaseElectricitySensor()).build();

        // When
        ManagedObjectRepresentation result = inventory.create(rep);

        // Then
        assertNotNull(result.getId());
        assertNotNull(result.get(ThreePhaseElectricitySensor.class));
    }

    @Test
    public void createManagedObjectWith2ThreePhaseElectricityFragments() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().with(new ThreePhaseElectricitySensor()).with(
                new ThreePhaseElectricitySensor()).build();

        // When
        ManagedObjectRepresentation result = inventory.create(rep);

        // Then
        assertNotNull(result.getId());
        assertNotNull(result.get(ThreePhaseElectricitySensor.class));
    }

    @Test
    public void createAndGetManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().build();
        ManagedObjectRepresentation created = inventory.create(rep);

        // When
        ManagedObjectRepresentation result = inventory.getManagedObject(created.getId()).get();

        // Then
        assertEquals(result.getId(), created.getId());
        assertEquals(result.getName(), created.getName());
        assertEquals(result.getType(), created.getType());
    }

    @Test
    public void createAndDeleteManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().build();
        ManagedObjectRepresentation result = inventory.create(rep);

        // When
        ManagedObject mo = inventory.getManagedObject(result.getId());
        mo.delete();

        // Then
        exception.expect(sdkException(NOT_FOUND));
        ManagedObject deletedMo = inventory.getManagedObject(result.getId());
        deletedMo.get();
    }

    @Test
    public void createAndUpdateManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().build();
        ManagedObjectRepresentation result = inventory.create(rep);

        // When
        Coordinate coordinate = new Coordinate(100.0, 10.0);
        result.set(coordinate);

        GId id = result.getId();
        result.setId(null);
        result.setLastUpdated(null);

        ManagedObjectRepresentation updated = inventory.getManagedObject(id).update(result);

        //Then
        assertNotNull(updated.getId());
        assertEquals(coordinate, updated.get(Coordinate.class));
    }

    @Test
    public void createAndUpdateManagedObjectByRemovingFragment() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().with(new Coordinate()).build();
        ManagedObjectRepresentation created = inventory.create(rep);

        // When
        created.set(null, Coordinate.class);
        GId id = created.getId();
        created.setId(null);
        created.setLastUpdated(null);
        ManagedObjectRepresentation updated = inventory.getManagedObject(id).update(created);

        // Then
        assertNotNull(updated.getId());
        assertNull(updated.get(Coordinate.class));
    }
    
    @Test
    @Ignore
    public void updatingManagedObjectByMultipleThreads() throws Exception {
        // Given
        int threads = 30;
        ExecutorService executor = newFixedThreadPool(threads);
        ManagedObjectRepresentation rep = aSampleMo().build();
        ManagedObjectRepresentation created = inventory.create(rep);
        final GId id = created.getId();
        List<Callable<Void>> tasks = buildUpdateTasks(threads, id);
        
        // When
        executor.invokeAll(tasks);
        
        // Then
        ManagedObjectRepresentation updated = checkNotDuplicated(id);
        checkUpdatedFragments(threads, updated);
    }

    private ManagedObjectRepresentation checkNotDuplicated(final GId id) throws SDKException {
        InventoryFilter byIdFilter = new InventoryFilter().byIds(asList(id));
        ManagedObjectCollectionRepresentation moCollection = inventory.getManagedObjectsByFilter(byIdFilter).get();
        List<ManagedObjectRepresentation> devices = moCollection.getManagedObjects();
        assertThat(devices.size(), is(1));
        return devices.get(0);
    }

    private void checkUpdatedFragments(int numberOfFragments, ManagedObjectRepresentation updated) {
        for(int i = 0; i < numberOfFragments; i++) {
            assertThat(updated.get(String.valueOf(i)), is(notNullValue()));
        }
    }

    private List<Callable<Void>> buildUpdateTasks(int numerOfTasks, final GId id) {
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for(int i = 0; i < numerOfTasks; i++) {
            final String fragmentName = String.valueOf(i);
            tasks.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    ManagedObjectRepresentation toUpdate = aSampleMo().build();
                    toUpdate.set("test", fragmentName);
                    inventory.getManagedObject(id).update(toUpdate);
                    return null;
                }
            });
        }
        return tasks;
    }

    @Test
    public void tryToGetNonExistentManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().withID(new GId("1")).build();

        // Then
        exception.expect(sdkException(NOT_FOUND));

        // When
        inventory.getManagedObject(rep.getId()).get();
    }

    @Test
    public void tryToDeleteNonExistentManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().withID(new GId("1")).build();

        // Then
        exception.expect(sdkException(NOT_FOUND));

        // When
        inventory.getManagedObject(rep.getId()).delete();
    }

    @Test
    public void tryToUpdateNonExistentManagedObject() throws Exception {
        // Given
        ManagedObjectRepresentation rep = aSampleMo().build();

        // Then
        exception.expect(sdkException(NOT_FOUND));

        // When
        inventory.getManagedObject(new GId("1")).update(rep);
    }

    @Test
    public void getAllWhenNoManagedObjectPresent() throws Exception {
        // When
        ManagedObjectCollectionRepresentation mos = inventory.getManagedObjectsByFilter(new InventoryFilter().byType("not_existing_mo_type")).get();

        // Then
        assertThat(mos.getManagedObjects(), is(Collections.<ManagedObjectRepresentation>emptyList()));
    }

    @Test
    public void getAllWhen2ManagedObjectArePresent() throws Exception {
        // Given
        ManagedObjectRepresentation rep1 = aSampleMo().withType("type1").build();
        ManagedObjectRepresentation rep2 = aSampleMo().withType("type1").build();

        // When
        inventory.create(rep1);
        inventory.create(rep2);

        // Then
        ManagedObjectCollectionRepresentation mos = inventory.getManagedObjectsByFilter(new InventoryFilter().byType("type1")).get();
        assertThat(mos.getManagedObjects().size(), is(2));
    }

    @Test
    public void addGetAndRemoveChildDevices() throws Exception {
        // Given
        ManagedObjectRepresentation parent = inventory.create(aSampleMo().withName("parent1").build());
        ManagedObjectRepresentation child1 = inventory.create(aSampleMo().withName("child11").build());
        ManagedObjectRepresentation child2 = inventory.create(aSampleMo().withName("child21").build());

        ManagedObjectReferenceRepresentation childRef1 = anMoRefRepresentationLike(MO_REF_REPRESENTATION).withMo(
                child1).build();

        // When
        ManagedObject parentMo = inventory.getManagedObject(parent.getId());
        parentMo.addChildDevice(childRef1);
        parentMo.addChildDevice(child2.getId());

        // Then
        ManagedObjectReferenceCollectionRepresentation refCollection = inventory.getManagedObject(
                parent.getId()).getChildDevices().get();

        List<ManagedObjectReferenceRepresentation> refs = refCollection.getReferences();
        Set<GId> childDeviceIDs = asSet(refs.get(0).getManagedObject().getId(), refs.get(1).getManagedObject().getId());
        assertThat(childDeviceIDs, is(asSet(child1.getId(), child2.getId())));
        
        // When
        parentMo.deleteChildDevice(child1.getId());
        parentMo.deleteChildDevice(child2.getId());

        // Then
        ManagedObjectReferenceCollectionRepresentation allChildDevices = inventory.getManagedObject(
                parent.getId()).getChildDevices().get();
        assertEquals(0, allChildDevices.getReferences().size());
    }

    @Test
    public void getPagedChildDevices() throws Exception {
        // Given
        ManagedObjectRepresentation parent = inventory.create(aSampleMo().withName("parent").build());
        ManagedObject parentMo = inventory.getManagedObject(parent.getId());

        for (int i=0; i<platform.getPageSize()+1; i++){
            ManagedObjectRepresentation child = inventory.create(aSampleMo().withName("child"+i).build());
            ManagedObjectReferenceRepresentation childRef = anMoRefRepresentationLike(MO_REF_REPRESENTATION).withMo(
                    child).build();
            parentMo.addChildDevice(childRef);
        }

        // When
        ManagedObjectReferenceCollection refCollection = inventory.getManagedObject(parent.getId()).getChildDevices();

        // Then
        assertCollectionPaged(refCollection);

    }

    private void assertCollectionPaged(ManagedObjectReferenceCollection refCollection) throws SDKException {
        assertThat(refCollection.get().getReferences().size(), is(platform.getPageSize()));
        assertThat(refCollection.get().getPageStatistics().getPageSize(), is(platform.getPageSize()));
        assertThat(refCollection.get().getPageStatistics().getCurrentPage(), is(1));
        assertThat(refCollection.get().getSelf(), containsString("pageSize=" + platform.getPageSize() + "&currentPage=1"));
        assertThat(refCollection.get().getNext(), containsString("pageSize=" + platform.getPageSize() + "&currentPage=2"));
        assertThat(refCollection.get().getPrev(), is(nullValue()));

        ManagedObjectReferenceCollectionRepresentation secondPage = refCollection.getPage(refCollection.get(), 2);
        assertThat(secondPage.getReferences().size(), is(1));
    }

    @Test
    public void addGetAndRemoveChildAssets() throws Exception {
        // Given
        ManagedObjectRepresentation parent = inventory.create(aSampleMo().withName("parent").build());
        ManagedObjectRepresentation child1 = inventory.create(aSampleMo().withName("child1").build());
        ManagedObjectRepresentation child2 = inventory.create(aSampleMo().withName("child2").build());

        ManagedObjectReferenceRepresentation childRef1 = anMoRefRepresentationLike(MO_REF_REPRESENTATION).withMo(
                child1).build();

        // When
        ManagedObject parentMo = inventory.getManagedObject(parent.getId());
        parentMo.addChildAssets(childRef1);
        parentMo.addChildAssets(child2.getId());

        // Then
        ManagedObjectReferenceCollectionRepresentation refCollection = inventory.getManagedObject(
                parent.getId()).getChildAssets().get();
        List<ManagedObjectReferenceRepresentation> refs = refCollection.getReferences();
        Set<GId> childDeviceIDs = asSet(refs.get(0).getManagedObject().getId(), refs.get(1).getManagedObject().getId());
        assertThat(childDeviceIDs, is(asSet(child1.getId(), child2.getId())));

        // When
        parentMo.deleteChildAsset(child1.getId());
        parentMo.deleteChildAsset(child2.getId());

        // Then
        ManagedObjectReferenceCollectionRepresentation allChildDevices = inventory.getManagedObject(
                parent.getId()).getChildAssets().get();
        assertEquals(0, allChildDevices.getReferences().size());
    }

@Test
    public void addGetAndRemoveChildAdditions() throws Exception {
        // Given
        ManagedObjectRepresentation parent = inventory.create(aSampleMo().withName("parent").build());
        ManagedObjectRepresentation child1 = inventory.create(aSampleMo().withName("child1").build());
        ManagedObjectRepresentation child2 = inventory.create(aSampleMo().withName("child2").build());

        ManagedObjectReferenceRepresentation childRef1 = anMoRefRepresentationLike(MO_REF_REPRESENTATION).withMo(
                child1).build();

        // When
        ManagedObject parentMo = inventory.getManagedObject(parent.getId());
        parentMo.addChildAdditions(childRef1);
        parentMo.addChildAdditions(child2.getId());

        // Then
        ManagedObjectReferenceCollectionRepresentation refCollection = inventory.getManagedObject(
                parent.getId()).getChildAdditions().get();
        List<ManagedObjectReferenceRepresentation> refs = refCollection.getReferences();
        Set<GId> childDeviceIDs = asSet(refs.get(0).getManagedObject().getId(), refs.get(1).getManagedObject().getId());
        assertThat(childDeviceIDs, is(asSet(child1.getId(), child2.getId())));

        // When
        parentMo.deleteChildAddition(child1.getId());
        parentMo.deleteChildAddition(child2.getId());

        // Then
        ManagedObjectReferenceCollectionRepresentation allChildAdditions = inventory.getManagedObject(
                parent.getId()).getChildAdditions().get();
        assertEquals(0, allChildAdditions.getReferences().size());
    }

    @Test
    public void getPagedChildAssets() throws Exception {
        // Given
        ManagedObjectRepresentation parent = inventory.create(aSampleMo().withName("parent").build());
        ManagedObject parentMo = inventory.getManagedObject(parent.getId());

        for (int i=0; i<platform.getPageSize()+1; i++){
            ManagedObjectRepresentation child = inventory.create(aSampleMo().withName("child"+i).build());
            ManagedObjectReferenceRepresentation childRef = anMoRefRepresentationLike(MO_REF_REPRESENTATION).withMo(
                    child).build();
            parentMo.addChildAssets(childRef);
        }

        // When
        ManagedObjectReferenceCollection refCollection = inventory.getManagedObject(parent.getId()).getChildAssets();

        // Then
        assertCollectionPaged(refCollection);

    }

    @Test
    public void getPagedChildAdditions() throws Exception {
        // Given
        ManagedObjectRepresentation parent = inventory.create(aSampleMo().withName("parent").build());
        ManagedObject parentMo = inventory.getManagedObject(parent.getId());

        for (int i=0; i<platform.getPageSize()+1; i++){
            ManagedObjectRepresentation child = inventory.create(aSampleMo().withName("child"+i).build());
            ManagedObjectReferenceRepresentation childRef = anMoRefRepresentationLike(MO_REF_REPRESENTATION).withMo(
                    child).build();
            parentMo.addChildAdditions(childRef);
        }

        // When
        ManagedObjectReferenceCollection refCollection = inventory.getManagedObject(parent.getId()).getChildAdditions();

        // Then
        assertCollectionPaged(refCollection);

    }

    @Test
    public void queryWithManagedObjectType() throws Exception {
        // Given
        inventory.create(aSampleMo().withType("typeA").withName("A1").build());
        inventory.create(aSampleMo().withType("typeA").withName("A2").build());
        inventory.create(aSampleMo().withType("typeB").withName("B").build());

        // When
        InventoryFilter filterA = new InventoryFilter().byType("typeA");
        ManagedObjectCollectionRepresentation typeAMos = inventory.getManagedObjectsByFilter(filterA).get();

        // Then
        assertThat(typeAMos.getManagedObjects().size(), is(2));

        // When
        InventoryFilter filterB = new InventoryFilter().byType("typeB");
        ManagedObjectCollectionRepresentation typeBMos = inventory.getManagedObjectsByFilter(filterB).get();

        // Then
        assertThat(typeBMos.getManagedObjects().size(), is(1));
    }

    @Test
    public void bulkQuery() throws Exception {
        // Given
        ManagedObjectRepresentation mo1 = inventory.create(aSampleMo().withName("MO1").build());
        ManagedObjectRepresentation mo3 = inventory.create(aSampleMo().withName("MO3").build());
        inventory.create(aSampleMo().withName("MO2").build());

        // When
        ManagedObjectCollectionRepresentation moCollection = inventory.getManagedObjectsByFilter(new InventoryFilter().byIds(asList(mo3.getId(), mo1.getId()))).get();

        // Then
        List<ManagedObjectRepresentation> mos = moCollection.getManagedObjects();
        assertThat(mos.size(), is(2));
      //TODO: Fix ordering when fixed on mongo
        assertThat(mos.get(0).getName(), isOneOf("MO1", "MO3")); 
        assertThat(mos.get(1).getName(), isOneOf("MO1", "MO3"));

    }

    @Test
    public void queryWithFragmentType() throws Exception {
        // Given
        inventory.create(aSampleMo().withName("MO1").with(new Coordinate()).build());
        inventory.create(aSampleMo().withName("MO2").with(new SecondFragment()).build());

        // When
        InventoryFilter filter = new InventoryFilter().byFragmentType(SecondFragment.class);
        ManagedObjectCollectionRepresentation coordinates = inventory.getManagedObjectsByFilter(filter).get();

        // Then
        assertThat(coordinates.getManagedObjects().size(), is(1));
    }

    @Test
    public void getAllWhen20ManagedObjectsPresent() throws Exception {
        // Given
        int numOfMos = 20;
        for (int i = 0; i < numOfMos; i++) {
            inventory.create(aSampleMo().withName("MO" + i).with(new Coordinate()).build());
        }

        // When
        ManagedObjectCollectionRepresentation mos = inventory.getManagedObjects().get();

        // Then
        assertThat(mos.getPageStatistics().getTotalPages(),  is(greaterThanOrEqualTo(4)));

        // When
        ManagedObjectCollectionRepresentation secondPage = inventory.getManagedObjects().getPage(mos, 2);

        // Then
        assertThat(secondPage.getPageStatistics().getCurrentPage(), is(2));

        // When
        ManagedObjectCollectionRepresentation thirdPage = inventory.getManagedObjects().getNextPage(secondPage);

        // Then
        assertThat(thirdPage.getPageStatistics().getCurrentPage(), is(3));

        // When
        ManagedObjectCollectionRepresentation firstPage = inventory.getManagedObjects().getPreviousPage(secondPage);
        assertThat(firstPage.getPageStatistics().getCurrentPage(), is(1));
    }

    private void deleteMOs(List<ManagedObjectRepresentation> mosOn1stPage) throws SDKException {
        for (ManagedObjectRepresentation mo : mosOn1stPage) {
            inventory.getManagedObject(mo.getId()).delete();
        }
    }

    private List<ManagedObjectRepresentation> getMOsFrom1stPage() throws SDKException {
        return inventory.getManagedObjects().get().getManagedObjects();
    }

    private static ManagedObjectRepresentationBuilder aSampleMo() {
        return anMoRepresentationLike(MO_REPRESENTATION);
    }

    private static <T> Set<T> asSet(T... items) {
        return new HashSet<T>(asList(items));
    }
}
