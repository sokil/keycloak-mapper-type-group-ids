/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.sokil.keycloak.protocol.oidc.mappers;

import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.*;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps user group ids
 */
public class GroupIdMapperType extends AbstractOIDCProtocolMapper implements
        OIDCAccessTokenMapper,
        OIDCIDTokenMapper,
        UserInfoTokenMapper {

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, GroupMembershipMapper.class);
    }

    public static final String PROVIDER_ID = "oidc-group-ids-mapper";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Group Ids";
    }

    @Override
    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getHelpText() {
        return "Map user group ids";
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    /**
     * Adds the group ids information to the {@link IDToken#otherClaims}.
     */
    protected void setClaim(
            IDToken token,
            ProtocolMapperModel mappingModel,
            UserSessionModel userSession,
            KeycloakSession keycloakSession,
            ClientSessionContext clientSessionCtx
    ) {
        Function<GroupModel, String> toGroupRepresentation = GroupModel::getId;
        List<String> membership = userSession.getUser().getGroupsStream().map(toGroupRepresentation).collect(Collectors.toList());
        String protocolClaim = mappingModel.getConfig().get(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME);
        token.getOtherClaims().put(protocolClaim, membership);
    }

    public static ProtocolMapperModel create(
            String name,
            String tokenClaimName,
            boolean consentRequired, String consentText,
            boolean accessToken, boolean idToken
    ) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<String, String>();
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, tokenClaimName);

        if (accessToken) {
            config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        }

        if (idToken) {
            config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        }

        mapper.setConfig(config);

        return mapper;
    }
}
