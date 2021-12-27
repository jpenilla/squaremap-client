package net.pl3x.map.fabric.util;

import java.util.UUID;

public record WorldInfo(UUID uuid, String name, int zoomMax, int zoomDefault, int zoomExtra) {
}
