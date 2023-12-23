package com.refinedmods.refinedsites.playbook;

import com.refinedmods.refinedsites.model.Component;

import java.util.stream.Stream;

@FunctionalInterface
interface ComponentFactory {
    Stream<Component> getComponents();
}
