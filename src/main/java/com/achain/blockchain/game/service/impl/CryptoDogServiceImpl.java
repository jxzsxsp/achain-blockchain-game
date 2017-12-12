package com.achain.blockchain.game.service.impl;

import com.achain.blockchain.game.conf.Config;
import com.achain.blockchain.game.domain.consts.CryptoDogEventType;
import com.achain.blockchain.game.domain.dto.AuctionDTO;
import com.achain.blockchain.game.domain.dto.DogDTO;
import com.achain.blockchain.game.domain.dto.TransactionDTO;
import com.achain.blockchain.game.domain.entity.BlockchainDogInfo;
import com.achain.blockchain.game.domain.entity.BlockchainDogOrder;
import com.achain.blockchain.game.domain.enums.OrderStatus;
import com.achain.blockchain.game.service.IBlockchainDogInfoService;
import com.achain.blockchain.game.service.IBlockchainDogOrderService;
import com.achain.blockchain.game.service.ICryptoDogService;
import com.achain.blockchain.game.utils.SymmetricEncoder;
import com.alibaba.fastjson.JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yujianjian
 * @since 2017-12-12 上午11:20
 */
@Service
@Slf4j
public class CryptoDogServiceImpl implements ICryptoDogService {

    private final static Long PER_BLOCK_TIME = 10_000L;

    @Autowired
    private Config config;
    @Autowired
    private IBlockchainDogInfoService blockchainDogInfoService;
    @Autowired
    private IBlockchainDogOrderService blockchainDogOrderService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateZeroDog(TransactionDTO transactionDTO) {
        log.info("generateZeroDog|transactionDTO={}", transactionDTO);
        String eventType = transactionDTO.getEventType();
        String eventParam = transactionDTO.getEventParam();
        if (CryptoDogEventType.GENERATE_SUCCESS.equals(eventType)) {
            String[] split = eventParam.split("\\|");
            DogDTO dogDTO = JSON.parseObject(split[0], DogDTO.class);
            AuctionDTO auctionDTO = JSON.parseObject(split[1], AuctionDTO.class);
            log.info("generateZeroDog|dogDTO={}|auctionDTO={}", dogDTO, auctionDTO);
            if (Objects.isNull(dogDTO) || Objects.isNull(auctionDTO)) {
                log.error("generateZeroDog|error|params miss");
                return;
            }

            String gene = SymmetricEncoder.AESDncode(config.encodeRules, dogDTO.getGene());
            long coolDown = transactionDTO.getTrxTime().getTime() + PER_BLOCK_TIME * dogDTO.getCooldown_end_block();

            BlockchainDogInfo blockchainDogInfo = new BlockchainDogInfo();
            blockchainDogInfo.setDogId(dogDTO.getId());
            blockchainDogInfo.setOwner(dogDTO.getOwner());
            blockchainDogInfo.setGene(gene);
            blockchainDogInfo.setBirthTime(new Date(dogDTO.getBirth_time()));
            blockchainDogInfo.setCooldownEndTime(new Date(coolDown));
            blockchainDogInfo.setMotherId(dogDTO.getMother_id());
            blockchainDogInfo.setFatherId(dogDTO.getFather_id());
            blockchainDogInfo.setGeneration(dogDTO.getGeneration());
            blockchainDogInfo.setFertility(dogDTO.getFertility() ? 1 : 0);
            blockchainDogInfo.setIsPregnant(dogDTO.getIs_pregnant() ? 1 : 0);
            blockchainDogInfoService.insert(blockchainDogInfo);

            long endTime = transactionDTO.getTrxTime().getTime() + auctionDTO.getDuration() * PER_BLOCK_TIME;
            BlockchainDogOrder blockchainDogOrder = new BlockchainDogOrder();
            blockchainDogOrder.setSeller(transactionDTO.getFromAddr());
            blockchainDogOrder.setDogId(auctionDTO.getTokenId());
            blockchainDogOrder.setStatus(OrderStatus.ON.getIntKey());
            blockchainDogOrder.setOrderId(auctionDTO.getTrx_id());
            blockchainDogOrder.setStartingPrice(auctionDTO.getStartingPrice());
            blockchainDogOrder.setEndingPrice(auctionDTO.getEndingPrice());
            blockchainDogOrder.setBeginTime(transactionDTO.getTrxTime());
            blockchainDogOrder.setEndTime(new Date(endTime));
            blockchainDogOrder.setTrxId(transactionDTO.getTrxId());
            blockchainDogOrderService.insert(blockchainDogOrder);
        }
    }

    @Override
    public void bid(TransactionDTO transactionDTO) {
        log.info("bid|transactionDTO={}", transactionDTO);
        String eventType = transactionDTO.getEventType();
        String eventParam = transactionDTO.getEventParam();
        if (CryptoDogEventType.BID_SUCCESS.equals(eventType)) {

        } else {

        }
    }

    @Override
    public void addAuction(TransactionDTO transactionDTO) {
        log.info("addAuction|transactionDTO={}", transactionDTO);
        String eventType = transactionDTO.getEventType();
        String eventParam = transactionDTO.getEventParam();
        if (CryptoDogEventType.ADD_AUCTION_SUCCESS.equals(eventType)) {

        } else {

        }
    }

    @Override
    public void cancelAuction(TransactionDTO transactionDTO) {
        log.info("cancelAuction|transactionDTO={}", transactionDTO);
        String eventType = transactionDTO.getEventType();
        String eventParam = transactionDTO.getEventParam();
        if (CryptoDogEventType.CANCEL_AUCTION_SUCCESS.equals(eventType)) {

        } else {

        }
    }
}