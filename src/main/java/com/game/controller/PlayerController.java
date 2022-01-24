package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.impl.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerServiceImpl playerService;



    @Autowired
    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public List<Player> players(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "title", required = false) String title,
                                @RequestParam(value = "race", required = false)Race race,
                                @RequestParam(value = "profession", required = false)Profession profession,
                                @RequestParam(value = "after", required = false) Long after,
                                @RequestParam(value = "before", required = false) Long before,
                                @RequestParam(value = "banned", required = false) Boolean banned,
                                @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return playerService.getAll(Specification.where(playerService.nameFilter(name)
                .and(playerService.experienceFilter(minExperience,maxExperience))
                .and(playerService.dateFilter(after, before))
                .and(playerService.usageFilter(banned))
                .and(playerService.levelFilter(minLevel,maxLevel))
                .and(playerService.titleFilter(title))
                .and(playerService.raceFilter(race))
                .and(playerService.professionFilter(profession))),pageable).getContent();
    }

    @RequestMapping(value = "/players/count", method = RequestMethod.GET)
    public int playersCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "race", required = false)Race race,
                            @RequestParam(value = "profession", required = false)Profession profession,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "banned", required = false) Boolean banned,
                            @RequestParam(value = "minLevel", required = false) Integer minLevel,
                            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                            @RequestParam(value = "minExperience", required = false) Integer minExperience,
                            @RequestParam(value = "maxExperience", required = false) Integer maxExperience) {
        return playerService.getAll(Specification.where(playerService.nameFilter(name)
                .and(playerService.experienceFilter(minExperience,maxExperience))
                .and(playerService.dateFilter(after, before))
                .and(playerService.usageFilter(banned))
                .and(playerService.levelFilter(minLevel,maxLevel))
                .and(playerService.titleFilter(title))
                .and(playerService.raceFilter(race))
                .and(playerService.professionFilter(profession)))).size();
    }

    @RequestMapping(value = "/players/{id}", method = RequestMethod.GET)
    public Player findPlayersById(@PathVariable Long id) {
        return playerService.findById(id);
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    public Player create(@RequestBody Player player){
        playerService.creat(player);
        return player;
    }

    @RequestMapping(value="/players/{id}", method=RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        Player player = playerService.findById(id);
        playerService.delete(player);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/players/{id}", method=RequestMethod.POST)
    public Player update(@RequestBody Player player, @PathVariable(value = "id", required = false) Long id) {
        return playerService.update(player,id);
    }
}

